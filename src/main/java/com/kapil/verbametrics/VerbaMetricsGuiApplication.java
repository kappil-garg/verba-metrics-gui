package com.kapil.verbametrics;

import com.kapil.verbametrics.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class VerbaMetricsGuiApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerbaMetricsGuiApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(VerbaMetricsGuiApplication.class)
                .headless(false)
                .run(args);
        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOGGER.error("Failed to set look and feel :: {}", e.getMessage());
            }
            MainWindow mainWindow = new MainWindow(context);
            mainWindow.setVisible(true);
        });
    }

}
