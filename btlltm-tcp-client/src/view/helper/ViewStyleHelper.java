package view.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;

public class ViewStyleHelper {
    // Enhanced color palette
    public static final Color PRIMARY_COLOR = new Color(56, 189, 248);    // Sky blue
    public static final Color SECONDARY_COLOR = new Color(71, 85, 105);   // Slate
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94);     // Green
    public static final Color DANGER_COLOR = new Color(239, 68, 68);      // Red
    public static final Color WARNING_COLOR = new Color(234, 179, 8);     // Yellow
    public static final Color BACKGROUND_COLOR = new Color(248, 250, 252); // Light gray
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 255); // White
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);       // Dark slate
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);  // Gray
    
    // Enhanced fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Table styling
    public static void styleTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(SUBHEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Selection colors
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
    }
    
    // Panel styling
    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(new CompoundBorder(
            new DropShadowBorder(Color.BLACK, 3, 0.2f),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }
    
    // ScrollPane styling
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new CompoundBorder(
            new RoundedBorder(8),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
    }

    public static void styleButton(JButton button, Color bgColor) {
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Round corners
        // button.setBorder(new RoundedBorder(10));
        
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

class DropShadowBorder extends AbstractBorder {
    private Color shadowColor;
    private int shadowSize;
    private float shadowOpacity;

    public DropShadowBorder(Color shadowColor, int shadowSize, float shadowOpacity) {
        this.shadowColor = new Color(
            shadowColor.getRed(),
            shadowColor.getGreen(),
            shadowColor.getBlue(),
            (int)(255 * shadowOpacity)
        );
        this.shadowSize = shadowSize;
        this.shadowOpacity = shadowOpacity;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create gradients for shadow effect
        for (int i = 0; i < shadowSize; i++) {
            float opacity = shadowOpacity * (float)(shadowSize - i) / shadowSize;
            Color currentColor = new Color(
                shadowColor.getRed(),
                shadowColor.getGreen(),
                shadowColor.getBlue(),
                (int)(255 * opacity)
            );
            g2.setColor(currentColor);
            
            // Draw shadow on all sides
            g2.drawRoundRect(
                x + i, 
                y + i, 
                width - (i * 2), 
                height - (i * 2), 
                10, 
                10
            );
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}