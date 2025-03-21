import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuizApp extends JFrame {
    private String[] questions = {
        "What is the capital of France?",
        "What is 2 + 2?",
        "Who wrote 'To Kill a Mockingbird'?",
        "What is the boiling point of water in Celsius?",
        "What is the largest planet in our solar system?"
    };

    private String[][] options = {
        {"London", "Paris", "Berlin", "Madrid"},
        {"3", "4", "5", "6"},
        {"Harper Lee", "J.K. Rowling", "Ernest Hemingway", "Mark Twain"},
        {"90°C", "100°C", "110°C", "120°C"},
        {"Earth", "Mars", "Jupiter", "Saturn"}
    };

    private char[] answers = {'B', 'B', 'A', 'B', 'C'};
    private int currentQuestion = 0;
    private int score = 0;
    private Timer timer;
    private int timeLeft = 10;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JLabel timerLabel;
    private JButton nextButton;
    private JLabel feedbackLabel;
    private JPanel quizPanel;
    private JPanel feedbackPanel;
    private JPanel optionsPanel;
    private JPanel mainPanel; // Added to manage card layout within quiz panel

    public QuizApp() {
        setTitle("Quiz Application");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        setResizable(false);

        // Create login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        add(loginPanel, "Login");

        // Set up the quiz panel
        quizPanel = new JPanel(new BorderLayout());

        // Set up the question panel with gradient background
        JPanel questionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(52, 152, 219), 0, getHeight(), new Color(41, 128, 185)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        questionPanel.setLayout(new BorderLayout());
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setForeground(Color.WHITE);
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        quizPanel.add(questionPanel, BorderLayout.NORTH);

        // Create a main panel to hold options and feedback with CardLayout
        mainPanel = new JPanel(new CardLayout());
        quizPanel.add(mainPanel, BorderLayout.CENTER);

        // Set up the options panel with rounded borders
        optionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setPaint(new Color(236, 240, 241));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setPaint(new Color(189, 195, 199));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(new Color(236, 240, 241));
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        // Add options panel to the card layout
        mainPanel.add(optionsPanel, "Options");

        // Set up the timer and next button panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(new Color(46, 204, 113)); // Green background
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        timerLabel = new JLabel("Time left: " + timeLeft);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        bottomPanel.add(timerLabel, BorderLayout.WEST);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(52, 152, 219));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        bottomPanel.add(nextButton, BorderLayout.EAST);

        quizPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set up the feedback panel with centered label and buttons
        feedbackPanel = new JPanel(new GridBagLayout());
        feedbackPanel.setBackground(new Color(245, 245, 245));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        feedbackLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackPanel.add(feedbackLabel, gbc);

        // Add feedback panel to the card layout
        mainPanel.add(feedbackPanel, "Feedback");

        add(quizPanel, "Quiz");

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft);
                if (timeLeft <= 0) {
                    timer.stop();
                    checkAnswer();
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                if ("user".equals(username) && "pass".equals(password)) {
                    CardLayout cl = (CardLayout) getContentPane().getLayout();
                    cl.show(getContentPane(), "Quiz");
                    loadQuestion();
                } else {
                    JOptionPane.showMessageDialog(QuizApp.this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private void loadQuestion() {
        if (currentQuestion >= questions.length) {
            showResults();
            return;
        }

        // Show options panel
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "Options");

        // Set question text
        questionLabel.setText((currentQuestion + 1) + ". " + questions[currentQuestion]);
        
        // Set options text
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char)('A' + i) + ". " + options[currentQuestion][i]);
            optionButtons[i].setSelected(false);
        }

        // Reset timer
        timeLeft = 10;
        timerLabel.setText("Time left: " + timeLeft);
        timer.restart();
        
        // Make next button visible
        nextButton.setVisible(true);
    }

    private void checkAnswer() {
        timer.stop();
        char selectedAnswer = ' ';
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = (char) ('A' + i);
                break;
            }
        }

        // Show feedback
        if (selectedAnswer == answers[currentQuestion]) {
            score++;
            feedbackLabel.setText("Correct!");
            feedbackLabel.setForeground(new Color(46, 204, 113)); // Green
        } else {
            feedbackLabel.setText("Incorrect! The correct answer is " + answers[currentQuestion] + ".");
            feedbackLabel.setForeground(new Color(231, 76, 60)); // Red
        }

        // Show feedback panel
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "Feedback");

        // Move to next question after a delay
        currentQuestion++;
        Timer feedbackTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadQuestion();
            }
        });
        feedbackTimer.setRepeats(false);
        feedbackTimer.start();
    }

    private void showResults() {
        // Remove any existing components from feedback panel
        feedbackPanel.removeAll();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Add feedback label
        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        feedbackPanel.add(feedbackLabel, gbc);
        
        // Create restart button
        JButton restartButton = new JButton("Restart Quiz");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setBackground(new Color(52, 152, 219));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetQuiz();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 10, 10, 5);
        feedbackPanel.add(restartButton, gbc);
        
        // Create exit button
        JButton exitButton = new JButton("Exit Quiz");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 10, 10);
        feedbackPanel.add(exitButton, gbc);
        
        // Show the feedback panel for results
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "Feedback");
        
        // Update UI
        questionLabel.setText("Quiz Completed!");
        timerLabel.setText("Final Score: " + score + "/" + questions.length);
        nextButton.setVisible(false);

        // Set appropriate feedback message
        if (score == questions.length) {
            feedbackLabel.setText("Congratulations! Perfect score!");
            feedbackLabel.setForeground(new Color(46, 204, 113)); // Green
        } else if (score >= questions.length - 1) {
            feedbackLabel.setText("Great job! Almost perfect!");
            feedbackLabel.setForeground(new Color(46, 204, 113)); // Green
        } else if (score >= questions.length / 2) {
            feedbackLabel.setText("Good effort! Keep studying!");
            feedbackLabel.setForeground(new Color(230, 126, 34)); // Orange
        } else {
            feedbackLabel.setText("Needs improvement. Keep practicing!");
            feedbackLabel.setForeground(new Color(231, 76, 60)); // Red
        }
        
        // Repaint to make sure everything is displayed properly
        feedbackPanel.revalidate();
        feedbackPanel.repaint();
    }
    
    private void resetQuiz() {
        // Reset quiz state
        currentQuestion = 0;
        score = 0;
        
        // Reload first question
        loadQuestion();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizApp();
            }
        });
    }
}
