package view;

import view.ViewStyleHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.*;

public class ViewStyleHelper {
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(0, 122, 255);
    public static final Color SECONDARY_COLOR = new Color(64, 64, 64);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color DANGER_COLOR = new Color(255, 59, 48);
    public static final Color WARNING_COLOR = new Color(255, 204, 51);
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    
    // Font styles
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    public static void styleButton(JButton button, Color bgColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Round corners
        button.setBorder(new RoundedBorder(10));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brighten(bgColor, 0.2f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    public static void styleTextField(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(5),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setBackground(Color.WHITE);
    }
    
    public static void styleLabel(JLabel label, Font font) {
        label.setFont(font);
        label.setForeground(SECONDARY_COLOR);
    }
    
    public static void styleProgressBar(JProgressBar progressBar) {
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(new RoundedBorder(5));
        progressBar.setPreferredSize(new Dimension(progressBar.getWidth(), 25));
    }
    
    private static Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
    }
}

// Custom rounded border
class RoundedBorder extends AbstractBorder {
    private int radius;
    
    RoundedBorder(int radius) {
        this.radius = radius;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        g2.dispose();
    }
}
