/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.scripting.jirb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.demo.TextAreaReadline;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.runtime.load.LoadService;


public class SwingIRBConsole extends JFrame {
	private static SwingIRBConsole console = null;
	private static Thread consoleThread = null;

    public SwingIRBConsole(String title) {
        super(title);
    }

    public static void main(final String[] args) {
    	Thread t2 = startIrbPanelWithExceptions(args);
        try {
            t2.join();
        } catch (InterruptedException ie) {
            // ignore
        }
        System.exit(0);
    }

    /** Start a console in a separate thread, unless it is already running */
    public static Thread start(String[] args) {
    	if(args==null) args=new String[0];
    	if(consoleThread==null || !(consoleThread.isAlive())){
    		consoleThread = startIrbPanelWithExceptions(args);
    	}else if(console!=null){
    		console.setState(NORMAL);
    		console.toFront();
    	}
    	return consoleThread;
    }
    
    /**
     * Starts IRB Panel with exception handling
     *
     * @param shell
     * @return Thread that contains IRB
     * @author Lagutko_N
     */
    private static Thread startIrbPanelWithExceptions(final String[] args) {
        try {
            return start_thread(args);
        }
        catch (IOException e) {
            //TODO: handle this
            e.printStackTrace();
            return null;
        }
    }
    
    private static Thread start_thread(final String[] args) throws IOException {
        console = new SwingIRBConsole("JRuby IRB Console");

        console.getContentPane().setLayout(new BorderLayout());
        console.setSize(700, 600);

        JEditorPane text = new JTextPane();

        text.setMargin(new Insets(8,8,8,8));
        text.setCaretColor(new Color(0xa4, 0x00, 0x00));
        text.setBackground(new Color(0xf2, 0xf2, 0xf2));
        text.setForeground(new Color(0xa4, 0x00, 0x00));
        Font font = ScriptUtils.findFont("Monospaced", Font.PLAIN, 14, new String[] {"Monaco", "Andale Mono"});

        text.setFont(font);
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(text);
        pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        console.getContentPane().add(pane);
        console.validate();

        final TextAreaReadline tar = new TextAreaReadline(text, " Welcome to the JRuby IRB Console \n\n");
        console.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                tar.shutdown();
            }
        });

        final RubyInstanceConfig config = new RubyInstanceConfig() {{
            setInput(tar.getInputStream());
            setOutput(new PrintStream(tar.getOutputStream()));
            setError(new PrintStream(tar.getOutputStream()));
            setObjectSpaceEnabled(true); // useful for code completion inside the IRB
            setLoadServiceCreator(new LoadServiceCreator() {
                public LoadService create(Ruby runtime) {
                    return new EclipseLoadService(runtime);
                }
            });
            
            // The following modification forces IRB to ignore the fact that inside eclipse
            // the STDIN.tty? returns false, and IRB must continue to use a prompt
            List<String> argList = new ArrayList<String>();
            if(args!=null) for(String arg:args) argList.add(arg);
            argList.add("--prompt-mode");
            argList.add("default");
            argList.add("--readline");
            setArgv(argList.toArray(new String[0]));
        }};
        final Ruby runtime = Ruby.newInstance(config);

        // Now set the process ID inside ruby to the same as the java system id
        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
        runtime.getLoadService().init(ScriptUtils.makeLoadPath(null));

        tar.hookIntoRuntime(runtime);

        Thread t2 = new Thread() {
            public void run() {
                // set thread context JRuby classloader here, for the main thread
                try {
                    Thread.currentThread().setContextClassLoader(runtime.getJRubyClassLoader());
                } catch (SecurityException se) {
                    // can't set TC classloader
                    if (runtime.getInstanceConfig().isVerbose()) {
                        System.err.println("WARNING: Security restrictions disallowed setting context classloader for main thread.");
                    }
                }
                console.setVisible(true);
                runtime.evalScriptlet("require 'irb'; require 'irb/completion'; IRB.start");
                console.dispose();
            }
        };
        t2.start();
        return(t2);
    }

    /**
     *
     */
    private static final long serialVersionUID = 3746242973444417387L;

}
