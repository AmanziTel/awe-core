package org.amanzi.awe.script.jirb;

/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
/*
 * example snippet: embed Swing/AWT in SWT
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.awt.SWT_AWT;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;

public class SWTIRBConsole extends Composite {
    private static final long serialVersionUID = 3746242973444417387L;
	private SwingTextAreaReadline tar = null;
	
	public SWTIRBConsole(Composite parent) {
		super(parent, SWT.EMBEDDED);

		java.awt.Frame irbFrame = SWT_AWT.new_Frame(this);
		java.awt.Panel panel = new java.awt.Panel(new java.awt.BorderLayout());
		irbFrame.add(panel);
		JPanel irbPanel = new JPanel();
		panel.add(irbPanel);

		this.setLayout(new GridLayout(1, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		Shell shell = null;
		if(parent instanceof Shell) shell = (Shell)parent;
		tar = make_irb_panel(irbPanel,shell);
	}

	public void shutdown(){
		tar.shutdown();
	}

	public static void start(final Display display) {
		final Shell shell = new Shell(display);
		shell.setText("SWT and Swing/AWT Example");

		final SWTIRBConsole ex = new SWTIRBConsole(shell);
		
		Listener exitListener = new Listener() {
			public void handleEvent(Event e) {
                ex.shutdown();
				shell.dispose();
			}
		};
		shell.addListener(SWT.Close, exitListener);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		layout.horizontalSpacing = layout.verticalSpacing = 1;
		shell.setLayout(layout);

		shell.open();
	}
	
    private static SwingTextAreaReadline make_irb_panel(JPanel console, final Shell shell) {
        console.setLayout(new BorderLayout());
        console.setSize(700, 600);

        JEditorPane text = new JTextPane();

        text.setMargin(new Insets(8,8,8,8));
        text.setCaretColor(new Color(0xa4, 0x00, 0x00));
        text.setBackground(new Color(0xf2, 0xf2, 0xf2));
        text.setForeground(new Color(0xa4, 0x00, 0x00));
        Font font = IRBUtils.findFont("Monospaced", Font.PLAIN, 14, new String[] {"Monaco", "Andale Mono"});

        text.setFont(font);
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(text);
        pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        console.add(pane);
        console.validate();

        final SwingTextAreaReadline tar = new SwingTextAreaReadline(text, " Welcome to the JRuby IRB Console \n\n");

        final RubyInstanceConfig config = new RubyInstanceConfig() {{
            setInput(tar.getInputStream());
            setOutput(new PrintStream(tar.getOutputStream()));
            setError(new PrintStream(tar.getOutputStream()));
            setObjectSpaceEnabled(true); // useful for code completion inside the IRB
            
            // The following modification forces IRB to ignore the fact that inside eclipse
            // the STDIN.tty? returns false, and IRB must continue to use a prompt
            List<String> argList = new ArrayList<String>();
            argList.add("--prompt-mode");
            argList.add("default");
            argList.add("--readline");
            setArgv(argList.toArray(new String[0]));
        }};
        final Ruby runtime = Ruby.newInstance(config);

        // Now set the process ID inside ruby to the same as the java system id
        runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
        runtime.getLoadService().init(IRBUtils.makeLoadPath());

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
                runtime.evalScriptlet("require 'irb'; require 'irb/completion'; IRB.start");
                if(shell!=null){
	                shell.getDisplay().asyncExec(new Runnable(){
						@Override
						public void run() {
							tar.shutdown();
							shell.dispose();
						}
	                });
                }else{
                	tar.shutdown();
                }
            }
        };
        t2.start();
        return(tar);
    }

}
