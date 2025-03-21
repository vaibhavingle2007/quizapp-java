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

    public QuizApp() {
        setTitle("Enhanced Quiz Application");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

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

        add(questionPanel, BorderLayout.NORTH);

        // Set up the options panel with rounded borders
        JPanel optionsPanel = new JPanel() {
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

        add(optionsPanel, BorderLayout.CENTER);

        // Set up the timer, feedback, and next button panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(new Color(46, 204, 113)); // Green background
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        timerLabel = new JLabel("Time left: " + timeLeft);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        bottomPanel.add(timerLabel, BorderLayout.WEST);

        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setForeground(Color.WHITE);
        bottomPanel.add(feedbackLabel, BorderLayout.CENTER);

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

        add(bottomPanel, BorderLayout.SOUTH);

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

        loadQuestion();
        setVisible(true);
    }

    private void loadQuestion() {
        if (currentQuestion >= questions.length) {
            showResults();
            return;
        }

        questionLabel.setText(questions[currentQuestion]);
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options[currentQuestion][i]);
        }

        timeLeft = 10;
        timerLabel.setText("Time left: " + timeLeft);
        feedbackLabel.setText("");
        optionGroup.clearSelection();
        timer.start();
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

        if (selectedAnswer == answers[currentQuestion]) {
            score++;
            feedbackLabel.setText("Correct!");
        } else {
            feedbackLabel.setText("Wrong!");
        }

        currentQuestion++;
        loadQuestion();
    }

    private void showResults() {
        questionLabel.setText("Quiz completed!");
        timerLabel.setText("Your score: " + score + "/" + questions.length);
        for (JRadioButton button : optionButtons) {
            button.setVisible(false);
        }
        nextButton.setVisible(false);

        if (score == questions.length) {
            feedbackLabel.setText("Congratulations! Perfect score!");
        } else if (score >= questions.length - 1) {
            feedbackLabel.setText("Nice try! Almost perfect!");
        } else if (score >= questions.length / 2) {
            feedbackLabel.setText("Good effort! Keep studying!");
        } else {
            feedbackLabel.setText("Needs improvement. Keep practicing!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizApp();
            }
        });
    }
}
