package br.com.pilovieira.updater4j.ui;

import br.com.pilovieira.updater4j.Options;
import br.com.pilovieira.updater4j.core.Processor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static br.com.pilovieira.updater4j.Lang.msg;

class GuiUpdater extends JFrame implements UpdaterUi {

    private static final long serialVersionUID = 7155605208697318521L;

    private final JLabel textInfo;
    private final JLabel textProcess;
    private final JProgressBar progress;
    private final JButton btnCancel;
    private final Thread updaterThread;

    private final Processor processor;
    private final Thread animThread;

    public GuiUpdater(Options options) {
        setBounds(100, 100, 600, 250);
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true);

        JPanel mainPane = new JPanel();
        mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));

        JPanel upPane = new JPanel();
        upPane.setLayout(new BoxLayout(upPane, BoxLayout.LINE_AXIS));

        textInfo = new JLabel(msg("initializingRet"));
        textInfo.setFont(new Font(textInfo.getFont().getName(), Font.PLAIN, 28));
        upPane.add(textInfo);

        upPane.add(Box.createGlue());

        try {
            BufferedImage buff = ImageIO.read(options.logo == null ?
                    getClass().getResourceAsStream("/image/transparent.png") : options.logo);
            ImageIcon icon = new ImageIcon(buff);
            Image image = icon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
            JLabel picLabel = new JLabel(new ImageIcon(image));
            upPane.add(picLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPane.add(upPane);

        mainPane.add(Box.createRigidArea(new Dimension(0,10)));

        progress = new JProgressBar();
        mainPane.add(progress);

        mainPane.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel downPane = new JPanel();
        downPane.setLayout(new BoxLayout(downPane, BoxLayout.LINE_AXIS));

        textProcess = new JLabel(msg("initializingRet"));
        downPane.add(textProcess);

        downPane.add(Box.createHorizontalGlue());

        btnCancel = new JButton(msg("cancel"));
        btnCancel.addActionListener(e -> cancelAction());
        downPane.add(btnCancel);

        mainPane.add(downPane);

        mainPane.setBackground(Color.LIGHT_GRAY);
        upPane.setBackground(Color.LIGHT_GRAY);
        downPane.setBackground(Color.LIGHT_GRAY);

        setContentPane(mainPane);
        setLocationRelativeTo(null);

        Anim anim = new Anim(options);
        animThread = new Thread(anim, "Updater4j Anim");

        processor = new Processor(options, new UpdaterCallbackImpl());
        updaterThread = new Thread(processor, "Updater4j GUI UpdaterUi");

        setVisible(true);
    }

    @Override
    public void startUpdate() {
        updaterThread.start();
    }

    private void cancelAction() {
        processor.abort();
        animThread.interrupt();
        updaterThread.interrupt();
        dispose();
    }


    public class UpdaterCallbackImpl implements Processor.Callback {
        @Override
        public void onStart() {
            animThread.start();
        }

        @Override
        public void onFinish() {
            animThread.interrupt();
        }

        @Override
        public void onPostLaunch() {
            SwingUtilities.invokeLater(GuiUpdater.this::dispose);
        }

        @Override
        public void onFail(Exception ex) {
            SwingUtilities.invokeLater(() -> {
                textInfo.setText(msg("updateFailed"));
                textProcess.setText(ex.getMessage());
                btnCancel.setText(msg("close"));
            });
        }

        @Override
        public void setStatus(String status) {
            SwingUtilities.invokeLater(() -> textProcess.setText(status));
        }

        @Override
        public void setProgress(long done, long max) {
            SwingUtilities.invokeLater(() -> {
                progress.setMaximum((int) max);
                progress.setValue((int) done);
            });
        }
    }


    private class Anim implements Runnable {

        private Options options;

        public Anim(Options options) {
            this.options = options;
        }

        @Override
        public void run() {
            try {
                String msg = options.message == null ? msg("updating") : options.message;
                for (;;) {
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + "."));
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + ".."));
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + "..."));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {}
        }
    }

}
