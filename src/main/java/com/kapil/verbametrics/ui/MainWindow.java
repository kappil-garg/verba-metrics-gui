package com.kapil.verbametrics.ui;

import com.kapil.verbametrics.ui.panels.MLModelPanel;
import com.kapil.verbametrics.ui.panels.TextAnalysisPanel;
import com.kapil.verbametrics.ui.util.GuiConstants;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Verba Metrics GUI.
 * Sets up the primary JFrame and integrates various UI components.
 *
 * @author Kapil Garg
 */
public class MainWindow extends JFrame {

    private final ConfigurableApplicationContext applicationContext;

    /**
     * Constructor to initialize the main window.
     *
     * @param applicationContext the Spring application context
     */
    public MainWindow(ConfigurableApplicationContext applicationContext) {
        super(GuiConstants.WINDOW_TITLE);
        this.applicationContext = applicationContext;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GuiConstants.WINDOW_WIDTH, GuiConstants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(GuiConstants.MIN_WINDOW_WIDTH, GuiConstants.MIN_WINDOW_HEIGHT));
        setLocationRelativeTo(null);
        initUi();
    }

    /**
     * Initialize the UI components and layout.
     */
    private void initUi() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Text Analysis", new TextAnalysisPanel(applicationContext));
        tabs.addTab("ML Models", new MLModelPanel(applicationContext));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

}
