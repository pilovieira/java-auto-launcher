package br.com.pilovieira.updater4j.view;

import br.com.pilovieira.updater4j.Options;
import br.com.pilovieira.updater4j.core.Updater;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

import static br.com.pilovieira.updater4j.util.Lang.msg;

public class Updater4jFrame extends JFrame {

    private static final long serialVersionUID = 7155605208697318521L;

    private final JLabel textInfo;
    private final JLabel textProcess;
    private final JProgressBar progress;
    private final JButton btnCancel;
    private final Thread updaterThread;

    private final Updater updater;
    private final Thread animThread;

    public Updater4jFrame(Options options) {
        setTitle(options.launcherTitle);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
            ImageIcon icon = new ImageIcon(ImageIO.read(options.launcherLogo));
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

        updater = new Updater(options, new UpdaterCallbackImpl());
        updaterThread = new Thread(updater, "Updater4j Updater");
        updaterThread.start();

        setVisible(true);
    }

    private void cancelAction() {
        updater.abort();
        animThread.interrupt();
        updaterThread.interrupt();
        dispose();
    }


    public class UpdaterCallbackImpl implements Updater.Callback {
        @Override
        public void onStart() {
            animThread.start();
        }

        @Override
        public void onFinish() {
            animThread.interrupt();
        }

        @Override
        public void onPostRun() {
            SwingUtilities.invokeLater(() -> dispose());
        }

        @Override
        public void onFail(Exception ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, ex.getMessage(), msg("failed"), JOptionPane.ERROR_MESSAGE);

                textInfo.setText(msg("updateFailed"));
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
                String msg = options.updateMessage.isEmpty() ? msg("updating") : options.updateMessage;
                for (;;) {
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + "."));
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + ".."));
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() -> textInfo.setText(msg + "..."));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
