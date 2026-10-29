import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    static ArrayList<User> users = new ArrayList<User>();
    static ArrayList<MaintenanceRequest> requests = new ArrayList<MaintenanceRequest>();
    static ArrayList<MaintenanceHistory> history = new ArrayList<MaintenanceHistory>();
    static ArrayList<Facility> facilities = new ArrayList<Facility>();

    // EDIT THESE two values to your own boarding house name and address.
    static BoardingHouse house = new BoardingHouse("Sejahada BHouse", "Tagbilaran City, Bohol, Philippines");

    static final String[] FACILITY_STATES = {"Available", "Needs Repair", "Under Maintenance"};

    static JFrame frame;
    static JPanel mainPanel;
    static CardLayout cards;
    static User currentUser;
    static String dataFolder = "data/";
    static Color navy = new Color(24, 43, 70);
    static Color blue = new Color(48, 108, 181);
    static Color pale = new Color(244, 247, 251);

    public static void main(String[] args) {
        applyModernLookAndFeel();
        loadAll();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { createWindow(); showHome(); }
        });
    }

    // Visual-only setup: keeps the existing screens and business logic intact.
    static void applyModernLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            UIManager.put("control", new Color(244, 247, 251));
            UIManager.put("nimbusBase", new Color(25, 45, 75));
            UIManager.put("nimbusBlueGrey", new Color(226, 234, 244));
            UIManager.put("nimbusFocus", new Color(47, 105, 170));
            UIManager.put("text", new Color(28, 43, 62));
            UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.BOLD, 13));
            UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("PasswordField.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));
        } catch (Exception ex) {
            System.err.println("Could not apply modern theme: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Window and reusable UI helpers
    // ------------------------------------------------------------------

    static void createWindow() {
        frame = new JFrame("Boarding House Maintenance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 760);
        frame.setMinimumSize(new Dimension(980, 650));
        frame.setLocationRelativeTo(null);
        cards = new CardLayout();
        mainPanel = new JPanel(cards);
        mainPanel.setBackground(pale);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    static JPanel page(String title, String subtitle) {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(pale);
        page.setBorder(new EmptyBorder(30, 34, 30, 34));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(navy);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subLabel.setForeground(new Color(103, 117, 136));
        subLabel.setBorder(new EmptyBorder(7, 0, 0, 0));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        heading.add(titleLabel);
        heading.add(subLabel);
        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    static JPanel whiteCard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 230, 239)),
                new EmptyBorder(26, 26, 26, 26)));
        return panel;
    }

    static JButton button(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBackground(blue);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(blue),
                new EmptyBorder(13, 20, 13, 20)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        b.setPreferredSize(new Dimension(220, 52));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(38, 88, 145));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(blue);
            }
        });
        return b;
    }

    static JButton lightButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBackground(new Color(235, 241, 249));
        b.setForeground(navy);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 230, 239)),
                new EmptyBorder(13, 20, 13, 20)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        b.setPreferredSize(new Dimension(220, 52));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(220, 231, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(235, 241, 249));
            }
        });
        return b;
    }

    static void setScreen(String name, JPanel panel) {
        mainPanel.add(panel, name);
        cards.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // ------------------------------------------------------------------
    // Access control
    // ------------------------------------------------------------------

    // True only if this user is the one who is currently logged in.
    static boolean authorized(User user) {
        return user != null && currentUser == user;
    }

    // Every protected screen/action calls this first. If nobody (or the wrong
    // person) is logged in, the session is cleared and the user is sent home.
    static boolean requireLogin(User user) {
        if (authorized(user)) return true;
        currentUser = null;
        JOptionPane.showMessageDialog(frame, "Please log in to continue.", "Access Denied", JOptionPane.WARNING_MESSAGE);
        showHome();
        return false;
    }

    // ------------------------------------------------------------------
    // Home, login, signup
    // ------------------------------------------------------------------

    static void showHome() {
        JPanel p = new JPanel(new GridLayout(1, 2, 0, 0));
        p.setBackground(pale);


        JPanel hero = new JPanel();
        hero.setBackground(navy);
        hero.setBorder(new EmptyBorder(30, 40, 30, 40));
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));

// 1. LARGE LOGO AT THE TOP
File logoFile = new File("logo.png");

if (!logoFile.exists()) {
    logoFile = new File(
        "Boarding_House_Maintenance_Tracker_UI_Improved/logo.png"
    );
}

if (logoFile.exists()) {
    ImageIcon originalIcon = new ImageIcon(
        logoFile.getAbsolutePath()
    );

    Image scaledImage = originalIcon.getImage()
        .getScaledInstance(380, 380, Image.SCALE_SMOOTH);

    JLabel logo = new JLabel(new ImageIcon(scaledImage));
    logo.setAlignmentX(Component.LEFT_ALIGNMENT);
    logo.setBorder(new EmptyBorder(0, 0, 15, 0));

    hero.add(logo);
}

// 2. SYSTEM TITLE UNDER THE LOGO
JLabel title = new JLabel(
    "<html>Boarding House<br>Maintenance Tracker</html>"
);
title.setForeground(Color.WHITE);
title.setFont(new Font("SansSerif", Font.BOLD, 30));
title.setAlignmentX(Component.LEFT_ALIGNMENT);

hero.add(title);

// 3. SYSTEM DESCRIPTION
JLabel desc = new JLabel(
    "<html>A simple, organized way to report<br>" +
    "and track maintenance concerns.</html>"
);
desc.setForeground(new Color(225, 233, 243));
desc.setFont(new Font("SansSerif", Font.PLAIN, 15));
desc.setBorder(new EmptyBorder(12, 0, 18, 0));
desc.setAlignmentX(Component.LEFT_ALIGNMENT);

hero.add(desc);

// 4. BOARDING HOUSE NAME
JLabel brand = new JLabel("SEJADA BHOUSE");
brand.setForeground(new Color(180, 211, 247));
brand.setFont(new Font("SansSerif", Font.BOLD, 15));
brand.setAlignmentX(Component.LEFT_ALIGNMENT);

hero.add(brand);

// 5. BOARDING HOUSE LOCATION
JLabel address = new JLabel(house.getAddress());
address.setForeground(new Color(180, 211, 247));
address.setFont(new Font("SansSerif", Font.PLAIN, 13));
address.setBorder(new EmptyBorder(7, 0, 0, 0));
address.setAlignmentX(Component.LEFT_ALIGNMENT);

hero.add(address);

// 6. TENANT, STAFF, AND MAINTENANCE LABEL
JLabel note = new JLabel("TENANT  •  STAFF  •  MAINTENANCE");
note.setForeground(new Color(180, 211, 247));
note.setFont(new Font("SansSerif", Font.BOLD, 11));
note.setBorder(new EmptyBorder(25, 0, 0, 0));
note.setAlignmentX(Component.LEFT_ALIGNMENT);

hero.add(note);


        hero.add(brand);
        hero.add(address);
        hero.add(title);
        hero.add(desc);
        hero.add(note);

        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setBorder(new EmptyBorder(58, 48, 48, 48));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcome.setForeground(navy);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instructions = new JLabel("<html>Choose an option to get started<br>with your maintenance tracker.</html>");
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 15));
        instructions.setForeground(new Color(103, 117, 136));
        instructions.setBorder(new EmptyBorder(10, 0, 32, 0));
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton login = button("Login  →");
        JButton signup = lightButton("Create Account");
        JButton exit = lightButton("Exit Application");
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        signup.setAlignmentX(Component.LEFT_ALIGNMENT);
        exit.setAlignmentX(Component.LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(380, 58));
        signup.setMaximumSize(new Dimension(380, 58));
        exit.setMaximumSize(new Dimension(380, 58));

        login.addActionListener(e -> showLoginType());
        signup.addActionListener(e -> showSignupType());
        exit.addActionListener(e -> System.exit(0));

        right.add(welcome);
        right.add(instructions);
        right.add(login);
        right.add(Box.createVerticalStrut(14));
        right.add(signup);
        right.add(Box.createVerticalStrut(14));
        right.add(exit);
        right.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("Boarding House Maintenance Tracker");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(new Color(125, 137, 153));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(footer);

        p.add(hero);
        p.add(right);
        setScreen("home", p);

        
    }

    static void showLoginType() {
        JPanel p = page("Login", "Choose the account type you want to use.");
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        JPanel box = whiteCard();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(520, 350));

        JLabel label = new JLabel("Continue as");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(navy);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton tenant = button("Login as Tenant");
        JButton staff = button("Login as Staff");
        JButton back = lightButton("Back to Main Menu");
        tenant.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        staff.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        back.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        tenant.addActionListener(e -> login(false));
        staff.addActionListener(e -> login(true));
        back.addActionListener(e -> showHome());

        box.add(label);
        box.add(Box.createVerticalStrut(22));
        box.add(tenant);
        box.add(Box.createVerticalStrut(14));
        box.add(staff);
        box.add(Box.createVerticalStrut(14));
        box.add(back);
        wrapper.add(box);
        p.add(wrapper, BorderLayout.CENTER);
        setScreen("loginType", p);
    }

    static void login(boolean staffLogin) {
        String username = JOptionPane.showInputDialog(frame, "Enter your username:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (username == null) return;
        username = username.trim();
        User found = null;
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equals(username) && ((staffLogin && u instanceof Staff) || (!staffLogin && u instanceof Tenant))) {
                found = u; break;
            }
        }
        if (found == null) {
            JOptionPane.showMessageDialog(frame, "Username not found for this account type.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        while (true) {
            JPasswordField passwordField = new JPasswordField(18);
            int result = JOptionPane.showConfirmDialog(frame, passwordField, "Enter password for " + username, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            String password = new String(passwordField.getPassword());
            if (found.checkPassword(password)) {
                currentUser = found;
                JOptionPane.showMessageDialog(frame, "Welcome, " + found.getFullName() + "! Hope you're having a great day!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                if (found instanceof Tenant) showTenantDashboard((Tenant) found); else showStaffDashboard((Staff) found);
                return;
            }
            JOptionPane.showMessageDialog(frame, "Incorrect password. Please try again.", "Try Again", JOptionPane.WARNING_MESSAGE);
        }
    }

    static void showSignupType() {
        JPanel p = page("Create an Account", "Select the type of account you need.");
        JPanel box = whiteCard();
        box.setLayout(new GridLayout(0, 1, 12, 12));
        JButton tenant = button("Sign Up as Tenant");
        JButton staff = button("Sign Up as Staff");
        JButton back = lightButton("Back to Main Menu");
        tenant.addActionListener(e -> signup(false));
        staff.addActionListener(e -> signup(true));
        back.addActionListener(e -> showHome());
        box.add(tenant); box.add(staff); box.add(back);
        p.add(box, BorderLayout.CENTER);
        setScreen("signupType", p);
    }

    static String ask(String label) {
        while (true) {
            String value = JOptionPane.showInputDialog(frame, label);
            if (value == null) return null;
            value = value.trim();
            if (!value.isEmpty()) return value;
            JOptionPane.showMessageDialog(frame, "This field is required.", "Missing Information", JOptionPane.WARNING_MESSAGE);
        }
    }

    static void signup(boolean staffSignup) {
        String first = ask("First name:"); if (first == null) return;
        String last = ask("Last name:"); if (last == null) return;
        String email;
        while (true) {
            email = ask("Email address (must contain @ and end in .com):"); if (email == null) return;
            if (email.contains("@") && email.endsWith(".com")) break;
            JOptionPane.showMessageDialog(frame, "Please enter a valid email ending in .com.");
        }
        String room = "";
        if (!staffSignup) { room = ask("Room number:"); if (room == null) return; }
        String username;
        while (true) {
            username = ask("Choose a username:"); if (username == null) return;
            boolean used = false;
            for (int i = 0; i < users.size(); i++) if (users.get(i).getUsername().equals(username)) used = true;
            if (!used) break;
            JOptionPane.showMessageDialog(frame, "That username is already used. Choose another one.");
        }
        String password;
        while (true) {
            JPasswordField pf = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(frame, pf, "Create password (at least 8 characters)", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;
            password = new String(pf.getPassword());
            if (password.length() < 8) { JOptionPane.showMessageDialog(frame, "Password must be at least 8 characters."); continue; }
            JPasswordField confirm = new JPasswordField();
            result = JOptionPane.showConfirmDialog(frame, confirm, "Confirm password", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;
            if (!password.equals(new String(confirm.getPassword()))) { JOptionPane.showMessageDialog(frame, "Passwords do not match. Try again."); continue; }
            break;
        }
        String passwordHash = User.hashPassword(password);   // only the hash is kept
        String id;
        if (staffSignup) {
            id = nextId("S-", users);
            users.add(new Staff(id, first, last, email, username, passwordHash));
        } else {
            id = nextId("T-", users);
            users.add(new Tenant(id, first, last, email, username, passwordHash, room));
        }
        saveUsers();
        JOptionPane.showMessageDialog(frame, "Account created successfully!\nYour ID is: " + id + "\nYou can now log in.", "Account Created", JOptionPane.INFORMATION_MESSAGE);
        showHome();
    }

    // ------------------------------------------------------------------
    // Dashboards, sidebar, profile
    // ------------------------------------------------------------------

    static void showTenantDashboard(Tenant tenant) {
        if (!requireLogin(tenant)) return;
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(pale);
        p.add(sidebar("Tenant Dashboard", new String[]{"Dashboard", "My Profile", "Submit Maintenance Request", "View My Requests", "Logout"}, tenant), BorderLayout.WEST);

        JPanel content = page("Welcome, " + tenant.getFullName() + "!", "Here is your maintenance overview.");
        JPanel box = whiteCard();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel intro = new JLabel("Keep track of maintenance concerns in your room.");
        intro.setFont(new Font("SansSerif", Font.BOLD, 17));
        intro.setForeground(navy);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(new Color(239, 245, 252));
        info.setBorder(new EmptyBorder(18, 20, 18, 20));
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel idLabel = new JLabel("YOUR TENANT ID     " + tenant.getId());
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        idLabel.setForeground(navy);
        info.add(idLabel, BorderLayout.CENTER);

        JButton submit = button("+  Submit a Maintenance Request");
        JButton view = lightButton("View My Requests  →");
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        view.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        submit.addActionListener(e -> submitRequest(tenant));
        view.addActionListener(e -> viewMyRequests(tenant));

        box.add(intro);
        box.add(Box.createVerticalStrut(24));
        box.add(info);
        box.add(Box.createVerticalStrut(24));
        box.add(submit);
        box.add(Box.createVerticalStrut(14));
        box.add(view);
        content.add(box, BorderLayout.CENTER);
        p.add(content, BorderLayout.CENTER);
        setScreen("tenantDashboard", p);
    }

    static JPanel sidebar(String heading, String[] items, User user) {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(250, 600));
        side.setBackground(navy);
        side.setBorder(new EmptyBorder(28, 18, 24, 18));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MAINTENANCE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title2 = new JLabel("TRACKER");
        title2.setForeground(new Color(180, 211, 247));
        title2.setFont(new Font("SansSerif", Font.BOLD, 17));
        title2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(heading);
        subtitle.setForeground(new Color(180, 211, 247));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setBorder(new EmptyBorder(12, 0, 30, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(title);
        side.add(title2);
        side.add(subtitle);

        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            JButton b = new JButton(item);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setPreferredSize(new Dimension(210, 46));
            b.setBackground(navy);
            b.setForeground(Color.WHITE);
            b.setOpaque(true);
            b.setFocusPainted(false);
            b.setBorder(new EmptyBorder(12, 13, 12, 8));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    b.setBackground(new Color(42, 66, 99));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    b.setBackground(navy);
                }
            });
            b.addActionListener(e -> {
                if (!requireLogin(user)) return;
                if (user instanceof Tenant) {
                    Tenant t = (Tenant) user;
                    if (item.equals("Dashboard")) showTenantDashboard(t);
                    else if (item.equals("My Profile")) showProfile(t);
                    else if (item.equals("Submit Maintenance Request")) submitRequest(t);
                    else if (item.equals("View My Requests")) viewMyRequests(t);
                    else logout();
                } else {
                    Staff s = (Staff) user;
                    if (item.equals("Dashboard")) showStaffDashboard(s);
                    else if (item.equals("My Profile")) showProfile(s);
                    else if (item.equals("Maintenance Requests")) showStaffRequests(s);
                    else if (item.equals("Update Request Status")) updateRequest(s);
                    else if (item.equals("Delete Request")) deleteRequest(s);
                    else if (item.equals("Maintenance History")) showHistory(s);
                    else if (item.equals("Facility Management")) showFacilities(s);
                    else logout();
                }
            });
            side.add(b);
            side.add(Box.createVerticalStrut(7));
        }
        side.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("PF101 • Maintenance System");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setForeground(new Color(155, 177, 204));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(footer);
        return side;
    }

    static void showProfile(User user) {
        if (!requireLogin(user)) return;
        JPanel p = dashboardPage(user, "My Profile", "Your account information.");
        JPanel box = whiteCard();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        addProfileRow(box, "FULL NAME", user.getFullName());
        addProfileRow(box, "EMAIL ADDRESS", user.getEmail());
        addProfileRow(box, user instanceof Tenant ? "TENANT ID" : "STAFF ID", user.getId());
        if (user instanceof Tenant) addProfileRow(box, "ROOM NUMBER", ((Tenant) user).getRoomNumber());
        p.add(box, BorderLayout.CENTER); setScreen("profile", p);
    }

    static void addProfileRow(JPanel box, String labelText, String valueText) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(103, 117, 136));
        JLabel value = new JLabel(valueText == null ? "" : valueText);
        value.setFont(new Font("SansSerif", Font.PLAIN, 16));
        value.setForeground(navy);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(label);
        row.add(Box.createVerticalStrut(6));
        row.add(value);
        row.setBorder(new EmptyBorder(0, 0, 22, 0));
        box.add(row);
    }

    static JPanel dashboardPage(User user, String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(pale);
        String[] tenantItems = {"Dashboard", "My Profile", "Submit Maintenance Request", "View My Requests", "Logout"};
        String[] staffItems = {"Dashboard", "My Profile", "Maintenance Requests", "Update Request Status", "Delete Request", "Maintenance History", "Facility Management", "Logout"};
        p.add(sidebar(user instanceof Tenant ? "Tenant Dashboard" : "Staff Dashboard", user instanceof Tenant ? tenantItems : staffItems, user), BorderLayout.WEST);
        p.add(page(title, subtitle), BorderLayout.CENTER);
        return p;
    }

    // ------------------------------------------------------------------
    // Tenant features
    // ------------------------------------------------------------------

    static void submitRequest(Tenant tenant) {
        if (!requireLogin(tenant)) return;
        String[] issues = {"Plumbing", "Electrical", "Cleaning", "Appliance", "Structural", "Other"};
        JComboBox<String> issue = new JComboBox<String>(issues);
        JTextArea desc = new JTextArea(4, 22); desc.setLineWrap(true); desc.setWrapStyleWord(true);
        JComboBox<String> priority = new JComboBox<String>(new String[]{"Low", "Medium", "High", "Urgent"});
        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Issue type:")); form.add(issue); form.add(new JLabel("Explain the problem:")); form.add(new JScrollPane(desc)); form.add(new JLabel("Priority:")); form.add(priority);
        Object[] options = {"Submit Request", "Back"};
        int choice = JOptionPane.showOptionDialog(frame, form, "Submit Maintenance Request", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice != 0) { showTenantDashboard(tenant); return; }
        if (desc.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(frame, "Please explain the problem."); submitRequest(tenant); return; }
        String id = nextId("M-", requests);
        MaintenanceRequest request = new MaintenanceRequest(id, tenant.getId(), tenant.getFullName(), tenant.getRoomNumber(), (String) issue.getSelectedItem(), desc.getText().trim(), (String) priority.getSelectedItem(), LocalDate.now().toString());
        requests.add(request); saveRequests();
        JOptionPane.showMessageDialog(frame, "Request submitted successfully!\nRequest ID: " + id + "\nStatus: PENDING");
        showTenantDashboard(tenant);
    }

    static void viewMyRequests(Tenant tenant) {
        if (!requireLogin(tenant)) return;
        ArrayList<MaintenanceRequest> mine = new ArrayList<MaintenanceRequest>();
        for (int i = 0; i < requests.size(); i++) if (requests.get(i).getTenantId().equals(tenant.getId())) mine.add(requests.get(i));
        if (mine.size() == 0) {
            JPanel p = dashboardPage(tenant, "My Requests", "Your maintenance requests will appear here.");
            JPanel box = whiteCard();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            JLabel empty = new JLabel("You do not have any maintenance requests yet.");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 16));
            JButton back = lightButton("Back to Dashboard");
            back.addActionListener(e -> showTenantDashboard(tenant));
            box.add(empty); box.add(Box.createVerticalStrut(20)); box.add(back);
            p.add(box, BorderLayout.CENTER);
            setScreen("myRequestsEmpty", p);
            return;
        }
        String[] choices = new String[mine.size()];
        for (int i = 0; i < mine.size(); i++) choices[i] = requestSummary(mine.get(i));
        String selected = (String) JOptionPane.showInputDialog(frame, "Your maintenance requests:\nSelect a PENDING request if you want to cancel it.", "My Requests", JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]);
        if (selected == null) { showTenantDashboard(tenant); return; }
        int index = -1; for (int i = 0; i < choices.length; i++) if (choices[i].equals(selected)) index = i;
        if (index >= 0) {
            MaintenanceRequest r = mine.get(index);
            if (r.getStatus().equals("PENDING")) {
                int cancel = JOptionPane.showConfirmDialog(frame, "Cancel request " + r.getId() + "?", "Cancel Request", JOptionPane.YES_NO_OPTION);
                if (cancel == JOptionPane.YES_OPTION) { requests.remove(r); saveRequests(); JOptionPane.showMessageDialog(frame, "Request cancelled."); }
            } else JOptionPane.showMessageDialog(frame, "Only PENDING requests can be cancelled. Current status: " + r.getStatus());
        }
        showTenantDashboard(tenant);
    }

    static String requestSummary(MaintenanceRequest r) {
        return r.getId() + " | " + r.getIssueType() + " | " + r.getStatus() + " | " + r.getDate();
    }

    // ------------------------------------------------------------------
    // Staff features
    // ------------------------------------------------------------------

    static void showStaffDashboard(Staff staff) {
        if (!requireLogin(staff)) return;
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(pale);
        String[] items = {"Dashboard", "My Profile", "Maintenance Requests", "Update Request Status", "Delete Request", "Maintenance History", "Facility Management", "Logout"};
        p.add(sidebar("Staff Dashboard", items, staff), BorderLayout.WEST);

        JPanel content = page("Welcome, " + staff.getFullName() + "!", "Monitor incoming maintenance concerns.");
        JPanel box = whiteCard();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel intro = new JLabel("Maintenance overview");
        intro.setFont(new Font("SansSerif", Font.BOLD, 18));
        intro.setForeground(navy);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        stats.add(statCard("PENDING", String.valueOf(countStatus("PENDING")), new Color(255, 246, 224)));
        stats.add(statCard("IN PROGRESS", String.valueOf(countStatus("IN PROGRESS")), new Color(229, 240, 255)));
        stats.add(statCard("FACILITIES", String.valueOf(facilities.size()), new Color(229, 247, 238)));

        JLabel helper = new JLabel("<html>Use the menu to review requests, update their status, and check completed maintenance.</html>");
        helper.setFont(new Font("SansSerif", Font.PLAIN, 14));
        helper.setForeground(new Color(103, 117, 136));
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(intro);
        box.add(Box.createVerticalStrut(22));
        box.add(stats);
        box.add(Box.createVerticalStrut(24));
        box.add(helper);
        content.add(box, BorderLayout.CENTER);
        p.add(content, BorderLayout.CENTER);
        setScreen("staffDashboard", p);
    }

    static JPanel statCard(String labelText, String valueText, Color tint) {
        JPanel card = new JPanel();
        card.setBackground(tint);
        card.setBorder(new EmptyBorder(18, 16, 18, 16));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(80, 95, 115));
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 28));
        value.setForeground(navy);
        card.add(label);
        card.add(Box.createVerticalStrut(10));
        card.add(value);
        return card;
    }

    static int countStatus(String status) {
        int count = 0;
        for (int i = 0; i < requests.size(); i++) if (requests.get(i).getStatus().equals(status)) count++;
        return count;
    }

    static void showStaffRequests(Staff staff) {
        if (!requireLogin(staff)) return;
        StringBuilder text = new StringBuilder("PENDING REQUESTS\n\n");
        boolean any = false;
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getStatus().equals("PENDING")) { text.append(requests.get(i).getDetails()).append("\n\n"); any = true; }
        }
        if (!any) text.append("No pending requests.\n");
        text.append("\nIN PROGRESS REQUESTS\n\n"); any = false;
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getStatus().equals("IN PROGRESS")) { text.append(requests.get(i).getDetails()).append("\n\n"); any = true; }
        }
        if (!any) text.append("No requests in progress.\n");
        showTextPage(staff, "Maintenance Requests", "Pending and in-progress requests.", text.toString(), "staffRequests");
    }

    static void showTextPage(User user, String title, String subtitle, String text, String key) {
        if (!requireLogin(user)) return;
        JPanel p = dashboardPage(user, title, subtitle);
        JTextArea area = new JTextArea(text); area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 13)); area.setLineWrap(true); area.setWrapStyleWord(true);
        JPanel card = whiteCard(); card.setLayout(new BorderLayout()); card.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER); setScreen(key, p);
    }

    static void updateRequest(Staff staff) {
        if (!requireLogin(staff)) return;
        if (requests.size() == 0) { JOptionPane.showMessageDialog(frame, "There are no maintenance requests to update."); showStaffDashboard(staff); return; }
        MaintenanceRequest r = chooseRequest("Choose a request to update:");
        if (r == null) { showStaffDashboard(staff); return; }
        String[] statuses = {"PENDING", "IN PROGRESS", "COMPLETED"};
        String status = (String) JOptionPane.showInputDialog(frame, "Select new status for " + r.getId() + ":", "Update Status", JOptionPane.QUESTION_MESSAGE, null, statuses, r.getStatus());
        if (status == null) { showStaffDashboard(staff); return; }
        r.setStatus(status);
        if (status.equals("COMPLETED")) {
            boolean already = false;
            for (int i = 0; i < history.size(); i++) if (history.get(i).getRequestId().equals(r.getId())) already = true;
            if (!already) {
                history.add(new MaintenanceHistory(r.getId(), r.getTenantName(), r.getRoomNumber(), r.getIssueType(), r.getDescription(), r.getPriority(), r.getDate()));
                saveHistory();
            }
        }
        saveRequests();
        JOptionPane.showMessageDialog(frame, "Request status updated to " + status + ".");
        showStaffDashboard(staff);
    }

    static MaintenanceRequest chooseRequest(String prompt) {
        String[] options = new String[requests.size()];
        for (int i = 0; i < requests.size(); i++) options[i] = requestSummary(requests.get(i));
        String chosen = (String) JOptionPane.showInputDialog(frame, prompt, "Select Request", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return null;
        for (int i = 0; i < requests.size(); i++) if (requestSummary(requests.get(i)).equals(chosen)) return requests.get(i);
        return null;
    }

    static void deleteRequest(Staff staff) {
        if (!requireLogin(staff)) return;
        if (requests.size() == 0) { JOptionPane.showMessageDialog(frame, "There are no requests to delete."); showStaffDashboard(staff); return; }
        MaintenanceRequest r = chooseRequest("Choose a request to delete:");
        if (r != null && JOptionPane.showConfirmDialog(frame, "Delete request " + r.getId() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            requests.remove(r); saveRequests(); JOptionPane.showMessageDialog(frame, "Request deleted.");
        }
        showStaffDashboard(staff);
    }

    static void showHistory(Staff staff) {
        if (!requireLogin(staff)) return;
        StringBuilder text = new StringBuilder("COMPLETED MAINTENANCE HISTORY\n\n");
        if (history.size() == 0) text.append("No completed requests yet.");
        for (int i = 0; i < history.size(); i++) text.append(history.get(i).getDetails()).append("\n\n");
        showTextPage(staff, "Maintenance History", "Completed requests are listed here.", text.toString(), "history");
    }

    // ------------------------------------------------------------------
    // Facility management (staff)
    // ------------------------------------------------------------------

    static void showFacilities(Staff staff) {
        if (!requireLogin(staff)) return;
        JPanel p = dashboardPage(staff, "Facility Management", "Add, view, search, update, or delete a facility.");
        JPanel box = whiteCard(); box.setLayout(new GridLayout(0, 1, 12, 12));
        JButton add = button("Add Facility");
        JButton view = lightButton("View Facilities");
        JButton search = lightButton("Search Facility");
        JButton update = lightButton("Update Facility");
        JButton delete = lightButton("Delete Facility");
        add.addActionListener(e -> addFacility(staff));
        view.addActionListener(e -> viewFacilities(staff));
        search.addActionListener(e -> searchFacility(staff));
        update.addActionListener(e -> updateFacility(staff));
        delete.addActionListener(e -> deleteFacility(staff));
        box.add(add); box.add(view); box.add(search); box.add(update); box.add(delete);
        p.add(box, BorderLayout.CENTER);
        setScreen("facilities", p);
    }

    static void addFacility(Staff staff) {
        if (!requireLogin(staff)) return;
        String name = ask("Facility name:");
        if (name == null) { showFacilities(staff); return; }
        String status = (String) JOptionPane.showInputDialog(frame, "Choose facility status:", "Add Facility", JOptionPane.QUESTION_MESSAGE, null, FACILITY_STATES, FACILITY_STATES[0]);
        if (status == null) { showFacilities(staff); return; }
        String id = nextId("F-", facilities);
        facilities.add(new Facility(id, name, status));
        saveFacilities();
        JOptionPane.showMessageDialog(frame, "Facility added successfully!\nFacility ID: " + id);
        showFacilities(staff);
    }

    static void viewFacilities(Staff staff) {
        if (!requireLogin(staff)) return;
        StringBuilder text = new StringBuilder("FACILITIES\n\n");
        if (facilities.size() == 0) text.append("No facilities added yet.");
        for (int i = 0; i < facilities.size(); i++) text.append(facilities.get(i).toString()).append("\n\n");
        showTextPage(staff, "All Facilities", "Facility list.", text.toString(), "facilityList");
    }

    static Facility chooseFacility() {
        if (facilities.size() == 0) { JOptionPane.showMessageDialog(frame, "No facilities found."); return null; }
        String[] opts = new String[facilities.size()];
        for (int i = 0; i < facilities.size(); i++) opts[i] = facilities.get(i).getId() + " | " + facilities.get(i).getName() + " | " + facilities.get(i).getStatus();
        String selected = (String) JOptionPane.showInputDialog(frame, "Select a facility:", "Facilities", JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        if (selected == null) return null;
        for (int i = 0; i < facilities.size(); i++) if (opts[i].equals(selected)) return facilities.get(i);
        return null;
    }

    static void searchFacility(Staff staff) {
        if (!requireLogin(staff)) return;
        String term = ask("Enter facility ID or name to search:");
        if (term != null) {
            boolean found = false;
            for (int i = 0; i < facilities.size(); i++) {
                Facility f = facilities.get(i);
                if (f.getId().equalsIgnoreCase(term) || f.getName().toLowerCase().contains(term.toLowerCase())) {
                    JOptionPane.showMessageDialog(frame, f.toString());
                    found = true;
                }
            }
            if (!found) JOptionPane.showMessageDialog(frame, "No matching facility found.");
        }
        showFacilities(staff);
    }

    static void updateFacility(Staff staff) {
        if (!requireLogin(staff)) return;
        Facility f = chooseFacility();
        if (f != null) {
            String name = ask("Facility name:");
            if (name != null) f.setName(name);
            String status = (String) JOptionPane.showInputDialog(frame, "Choose status:", "Update Facility", JOptionPane.QUESTION_MESSAGE, null, FACILITY_STATES, f.getStatus());
            if (status != null) f.setStatus(status);
            saveFacilities();
            JOptionPane.showMessageDialog(frame, "Facility updated.");
        }
        showFacilities(staff);
    }

    static void deleteFacility(Staff staff) {
        if (!requireLogin(staff)) return;
        Facility f = chooseFacility();
        if (f != null && JOptionPane.showConfirmDialog(frame, "Delete " + f.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            facilities.remove(f);
            saveFacilities();
            JOptionPane.showMessageDialog(frame, "Facility deleted.");
        }
        showFacilities(staff);
    }

    static void logout() {
        currentUser = null;
        JOptionPane.showMessageDialog(frame, "You have been logged out.");
        showHome();
    }

    // ------------------------------------------------------------------
    // ID generator
    // ------------------------------------------------------------------

    static String nextId(String prefix, ArrayList<?> list) {
        int largest = 0;
        for (int i = 0; i < list.size(); i++) {
            String id = "";
            Object item = list.get(i);
            if (item instanceof User) id = ((User) item).getId();
            else if (item instanceof MaintenanceRequest) id = ((MaintenanceRequest) item).getId();
            else if (item instanceof Facility) id = ((Facility) item).getId();
            if (id.startsWith(prefix)) {
                try { int number = Integer.parseInt(id.substring(prefix.length())); if (number > largest) largest = number; } catch (NumberFormatException e) { }
            }
        }
        int next = largest + 1;
        String number;
        if (next < 10) number = "00" + next;
        else if (next < 100) number = "0" + next;
        else number = "" + next;
        return prefix + number;
    }

    // ------------------------------------------------------------------
    // File storage
    // ------------------------------------------------------------------

    static void loadAll() {
        File folder = new File(dataFolder);
        if (!folder.exists()) {
            File oldFolder = new File("src/data/");
            if (oldFolder.exists()) dataFolder = "src/data/";
            else folder.mkdirs();
        }
        loadUsers(); loadRequests(); loadHistory(); loadFacilities();
    }

    // Shows the problem to the user and prints it to the console.
    // (frame may still be null during start-up, which JOptionPane allows.)
    static void reportError(String message) {
        System.err.println(message);
        JOptionPane.showMessageDialog(frame, message, "File Error", JOptionPane.ERROR_MESSAGE);
    }

    static void warnSkipped(String fileName, int recordNumber) {
        System.err.println("Skipped invalid record #" + recordNumber + " in " + fileName);
    }

    // Reads a data file and splits every non-empty line on "|".
    // A missing file is normal on first run, so it just returns an empty list.
    static ArrayList<String[]> readRecords(String fileName) {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        File file = new File(dataFolder + fileName);
        if (!file.exists()) return rows;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                rows.add(line.split("\\|", -1));
            }
        } catch (IOException e) {
            reportError("Could not read " + fileName + ": " + e.getMessage());
        }
        return rows;
    }

    // Writes all lines to a data file; the file is always closed, even on error.
    static void writeRecords(String fileName, ArrayList<String> lines) {
        new File(dataFolder).mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFolder + fileName))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            reportError("Could not save " + fileName + ": " + e.getMessage());
        }
    }

    static void loadUsers() {
        boolean migrated = false;
        ArrayList<String[]> rows = readRecords("users.txt");
        for (int i = 0; i < rows.size(); i++) {
            String[] d = rows.get(i);
            boolean isTenant = d[0].equals("TENANT") && d.length >= 8;
            boolean isStaff = d[0].equals("STAFF") && d.length >= 7;
            if (!isTenant && !isStaff) { warnSkipped("users.txt", i + 1); continue; }
            String stored = d[6];
            if (!User.isHashed(stored)) {          // old file with a plain-text password
                stored = User.hashPassword(stored);
                migrated = true;
            }
            if (isTenant) users.add(new Tenant(d[1], d[2], d[3], d[4], d[5], stored, d[7]));
            else users.add(new Staff(d[1], d[2], d[3], d[4], d[5], stored));
        }
        if (migrated) saveUsers();                 // rewrite the file with hashes only
    }

    static void saveUsers() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            String common = u.getId() + "|" + u.getFirstName() + "|" + u.getLastName() + "|" + u.getEmail() + "|" + u.getUsername() + "|" + u.getPasswordHash();
            if (u instanceof Tenant) lines.add("TENANT|" + common + "|" + ((Tenant) u).getRoomNumber());
            else lines.add("STAFF|" + common);
        }
        writeRecords("users.txt", lines);
    }

    static void loadRequests() {
        ArrayList<String[]> rows = readRecords("maintenance_requests.txt");
        for (int i = 0; i < rows.size(); i++) {
            String[] d = rows.get(i);
            if (d.length < 9) { warnSkipped("maintenance_requests.txt", i + 1); continue; }
            MaintenanceRequest m = new MaintenanceRequest(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7]);
            m.setStatus(d[8]);
            requests.add(m);
        }
    }

    static void saveRequests() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < requests.size(); i++) lines.add(requests.get(i).toFile());
        writeRecords("maintenance_requests.txt", lines);
    }

    static void loadHistory() {
        ArrayList<String[]> rows = readRecords("maintenance_history.txt");
        for (int i = 0; i < rows.size(); i++) {
            String[] d = rows.get(i);
            if (d.length < 7) { warnSkipped("maintenance_history.txt", i + 1); continue; }
            history.add(new MaintenanceHistory(d[0], d[1], d[2], d[3], d[4], d[5], d[6]));
        }
    }

    static void saveHistory() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < history.size(); i++) lines.add(history.get(i).toFile());
        writeRecords("maintenance_history.txt", lines);
    }

    static void loadFacilities() {
        ArrayList<String[]> rows = readRecords("facilities.txt");
        for (int i = 0; i < rows.size(); i++) {
            String[] d = rows.get(i);
            if (d.length < 3) { warnSkipped("facilities.txt", i + 1); continue; }
            facilities.add(new Facility(d[0], d[1], d[2]));
        }
    }

    static void saveFacilities() {
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < facilities.size(); i++) lines.add(facilities.get(i).toFile());
        writeRecords("facilities.txt", lines);
    }
}
