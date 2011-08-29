package org.jruby.compiler.ir.dataflow.analyses;

import org.jruby.compiler.ir.IRClosure;
import org.jruby.compiler.ir.dataflow.DataFlowProblem;
import org.jruby.compiler.ir.dataflow.DataFlowVar;
import org.jruby.compiler.ir.dataflow.FlowGraphNode;
import org.jruby.compiler.ir.instructions.CallInstr;
import org.jruby.compiler.ir.instructions.Instr;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.MetaObject;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.BasicBlock;
import org.jruby.compiler.ir.representations.CFG;
import org.jruby.compiler.ir.representations.CFG.CFG_Edge;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class LiveVariableNode extends FlowGraphNode
{
/* ---------- Public fields, methods --------- */
    public LiveVariableNode(DataFlowProblem prob, BasicBlock n) { super(prob, n); }

    @Override
    public void init()
    {
        _setSize = _prob.getDFVarsCount();
        _in = new BitSet(_setSize);
    }

    private void addDFVar(Variable v)
    {
        LiveVariablesProblem lvp = (LiveVariablesProblem)_prob;
        if ((v != null) && (lvp.getDFVar(v) == null)) {
            lvp.addDFVar(v);
//            System.out.println("Adding df var for " + v + ":" + lvp.getDFVar(v)._id);
        }
    }

    public void buildDataFlowVars(Instr i)
    {
//        System.out.println("BV: Processing: " + i);
        addDFVar(i.getResult());
        for (Variable x: i.getUsedVariables())
            addDFVar(x);
    }

    public void initSolnForNode()
    {
        LiveVariablesProblem p = (LiveVariablesProblem)_prob;
        _tmp = new BitSet(_setSize);
        if (_bb == p.getCFG().getExitBB()) {
            Collection<Variable> lv = p.getVarsLiveOnExit();
            if ((lv != null) && !lv.isEmpty()) {
                for (Variable v: lv)
                    _tmp.set(p.getDFVar(v)._id);
            }
        }
    }

    public void compute_MEET(CFG_Edge edge, FlowGraphNode pred)
    {
//        System.out.println("computing meet for BB " + _bb.getID() + " with BB " + ((LiveVariableNode)pred)._bb.getID());
        // All variables live at the entry of 'pred' are also live at exit of this node
        _tmp.or(((LiveVariableNode)pred)._in);
    }

    private LiveVariablesProblem processClosure(IRClosure cl, Collection<Variable> liveOnEntry)
    {
        CFG c = cl.getCFG();
        LiveVariablesProblem lvp = new LiveVariablesProblem();
        lvp.initVarsLiveOnExit(liveOnEntry);
        lvp.setup(c);
        lvp.compute_MOP_Solution();
        c.setDataFlowSolution(lvp.getName(), lvp);

        return lvp;
    }

    public boolean applyTransferFunction()
    {
        LiveVariablesProblem lvp = (LiveVariablesProblem)_prob;

        _out = (BitSet)_tmp.clone();
//        System.out.println("Apply TF for BB " + _bb.getID());
//        System.out.println("After MEET, df state is:\n" + toString());

        // Traverse the instructions in this basic block in reverse order!
        List<Instr> instrs = _bb.getInstrs();
        ListIterator<Instr> it = instrs.listIterator(instrs.size());
        while (it.hasPrevious()) {
            Instr i = it.previous();
//            System.out.println("TF: Processing: " + i);

            // v is defined => It is no longer live before 'i'
            Variable v = i.getResult();
            if (v != null) {
                DataFlowVar dv = lvp.getDFVar(v);
                _tmp.clear(dv._id);
//                System.out.println("cleared live flag for: " + v);
            }

            // Check if 'i' is a call and uses a closure!
            // If so, we need to process the closure for live variable info.
            if (i instanceof CallInstr) {
                CallInstr c = (CallInstr) i;
                // SSS FIXME: This relies on the local opt. pass having run already
                // so that the built closure from the previous instr. is propagated to the call site here.
                // Formalize this dependency somewhere?
                Operand o = c.getClosureArg();
//                   System.out.println("Processing closure: " + o + "-------");
                if ((o != null) && (o instanceof MetaObject)) {
                    IRClosure cl = (IRClosure)((MetaObject)o).scope;
                    if (c.isLVADataflowBarrier()) {
                        processClosure(cl, lvp.getAllVars());

                        // Mark all variables live if 'c' is a dataflow barrier!
//                        System.out.println(".. call is a data flow barrier ..");
                        for (int j = 0; j < _setSize; j++)
                            _tmp.set(j);
                    }
                    else {
                        // Propagate current LVA state through the closure
                        // SSS FIXME: Think through this .. Is there any way out of having
                        // to recompute the entire lva for the closure each time through?

                        // 1. Collect variables live at this point.
                        List<Variable> liveVars = new ArrayList<Variable>();
                        for (int j = 0; j < _tmp.size(); j++) {
                            if (_tmp.get(j) == true) {
//                                System.out.println(lvp.getVariable(j) + " is live on exit of closure!");
                                liveVars.add(lvp.getVariable(j));
                            }
                        }
//                        System.out.println(" .. collected live on exit ..");

                        // 2. Run LVA on the closure
                        LiveVariablesProblem xlvp = processClosure(cl, liveVars);

//                        System.out.println("------- done with closure" + o);

                        // 3. Collect variables live on entry of the closure and merge that info into the current problem.
                        for (Variable y: xlvp.getVarsLiveOnEntry()) {
                            DataFlowVar dv = lvp.getDFVar(y);
                            // This can be null for vars used, but not defined!  Yes, the source program is buggy ..
                            if (dv != null) {
//                                System.out.println(y + " is live on entry of the closure!");
                                _tmp.set(dv._id);
                            }
                        } 
                    }
                }
                else if (c.isLVADataflowBarrier()) {
                    // Mark all variables live if 'c' is a dataflow barrier!
//                    System.out.println(".. call is a data flow barrier ..");
                    for (int j = 0; j < _setSize; j++)
                        _tmp.set(j);
                }
            }

            // Now, for all variables used by 'i' mark them live before 'i'
            for (Variable x: i.getUsedVariables()) {
                DataFlowVar dv = lvp.getDFVar(x);
                // This can be null for vars used, but not defined!  Yes, the source program is buggy ..
                if (dv != null) {
                    _tmp.set(dv._id);
//                    System.out.println("set live flag for: " + x);
                }
            }
        }

            // IN is the same!
        if (_tmp.equals(_in)) {
            return false;
        }
            // IN changed!
        else {
            _in = _tmp;
            return true;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("\tVars Live on Entry: ");
        int count = 0;
        for (int i = 0; i < _in.size(); i++) {
            if (_in.get(i) == true) {
                count++;
                buf.append(' ').append(i);
                if (count % 10 == 0)
                    buf.append("\t\n");
            }
        }

        if (count % 10 != 0)
            buf.append("\t\t");

        buf.append("\n\tVars Live on Exit: ");
        count = 0;
        for (int i = 0; i < _out.size(); i++) {
            if (_out.get(i) == true) {
                count++;
                buf.append(' ').append(i);
                if (count % 10 == 0)
                    buf.append("\t\n");
            }
        }

        if (count % 10 != 0)
            buf.append("\t\t");

        return buf.append('\n').toString();
    }

/* ---------- Protected / package fields, methods --------- */
    void markDeadInstructions()
    {
//        System.out.println("dead processing for " + _bb.getID());
        LiveVariablesProblem lvp = (LiveVariablesProblem)_prob;

		  if (_out == null) {
			  // _out cannot be null for reachable bbs! 
			  // This bb is unreachable! (or we have a mighty bug!)
			  // Mark everything dead in here!
			  for (Instr i: _bb.getInstrs())
				  i.markDead();

			  return;
		  }

        _tmp = (BitSet)_out.clone();

        // Traverse the instructions in this basic block in reverse order!
        // Mark as dead all instructions whose results are not used! 
        List<Instr> instrs = _bb.getInstrs();
        ListIterator<Instr> it = instrs.listIterator(instrs.size());
        while (it.hasPrevious()) {
            Instr i = it.previous();
//            System.out.println("DEAD?? " + i);
            Variable v = i.getResult();
            if (v != null) {
                DataFlowVar dv = lvp.getDFVar(v);
                    // If 'v' is not live at the instruction site, and it has no side effects, mark it dead!
                if ((_tmp.get(dv._id) == false) && !i.hasSideEffects()) {
//                    System.out.println("YES!");
                    i.markDead();
                    it.remove();
                }
                else if (_tmp.get(dv._id) == false) {
//                    System.out.println("NO!  has side effects! Op is: " + i.operation);
                }
                else {
//                    System.out.println("NO! LIVE result:" + v);
                    _tmp.clear(dv._id);
                }
            }
            else {
//                System.out.println("IGNORING! No result!");
            }

            if (i instanceof CallInstr) {
                CallInstr c = (CallInstr) i;
                if (c.isLVADataflowBarrier()) {
                    // Mark all variables live if 'c' is a dataflow barrier!
                    for (int j = 0; j < _setSize; j++)
                        _tmp.set(j);
                }
                else {
                    Operand o = c.getClosureArg();
                    if ((o != null) && (o instanceof MetaObject)) {
                        // 2. Run LVA on the closure
                        IRClosure cl = (IRClosure)((MetaObject)o).scope;
                        CFG x = cl.getCFG();
                        LiveVariablesProblem xlvp = (LiveVariablesProblem)x.getDataFlowSolution(lvp.getName());
                        // 3. Collect variables live on entry and merge that info into the current problem.
                        for (Variable y: xlvp.getVarsLiveOnEntry()) {
                            DataFlowVar dv = lvp.getDFVar(y);
                            // This can be null for vars used, but not defined!  Yes, the source program is buggy ..
                            if (dv != null)
                                _tmp.set(dv._id);
                        } 
                    }
                }
            }

            // Do not mark this instruction's operands live if the instruction itself is dead!
            if (!i.isDead()) {
               for (Variable x: i.getUsedVariables()) {
                   DataFlowVar dv = lvp.getDFVar(x);
                   if (dv != null)
                       _tmp.set(dv._id);
               }
            }
        }
    }

    BitSet getLiveInBitSet()  { return _in; }

    BitSet getLiveOutBitSet() { return _out; }

/* ---------- Private fields, methods --------- */
    private BitSet _in;         // Variables live at entry of this node
    private BitSet _out;        // Variables live at exit of node
    private BitSet _tmp;        // Temporary set of live variables
    private int    _setSize;    // Size of the "_in" and "_out" bit sets 
}
