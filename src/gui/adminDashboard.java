/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

// -------- Composite Pattern --------
// Component
interface StaffComponent {

    void showDetails();

    String getRole();
}

// Leaf
class StaffMember implements StaffComponent {

    private String name;
    private String role;

    public StaffMember(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public void showDetails() {
        System.out.println("Staff: " + name + " | Role: " + role);
    }

    @Override
    public String getRole() {
        return role;
    }
}

// Composite
class StaffGroup implements StaffComponent {

    private String groupName;
    private java.util.List<StaffComponent> staffList = new java.util.ArrayList<>();

    public StaffGroup(String groupName) {
        this.groupName = groupName;
    }

    public void add(StaffComponent staff) {
        staffList.add(staff);
    }

    public void remove(StaffComponent staff) {
        staffList.remove(staff);
    }

    @Override
    public void showDetails() {
        System.out.println("Group: " + groupName);
        for (StaffComponent staff : staffList) {
            staff.showDetails();
        }
    }

    @Override
    public String getRole() {
        return "Group";
    }

    public java.util.List<StaffComponent> getStaffList() {
        return staffList;
    }
}

//  Decorator Pattern Classes 
// Component
interface Bill {

    double getCost();

    String getDescription();
}

// Concrete Component
class BaseBill implements Bill {

    private double amount;

    public BaseBill(double amount) {
        this.amount = amount;
    }

    @Override
    public double getCost() {
        return amount;
    }

    @Override
    public String getDescription() {
        return "Base Bill = " + amount;
    }
}

// Abstract Decorator
abstract class BillDecorator implements Bill {

    protected Bill bill;

    public BillDecorator(Bill bill) {
        this.bill = bill;
    }

    @Override
    public double getCost() {
        return bill.getCost();
    }

    @Override
    public String getDescription() {
        return bill.getDescription();
    }
}

// Insurance Decorator
class InsuranceDecorator extends BillDecorator {

    private double coverage;

    public InsuranceDecorator(Bill bill, double coverage) {
        super(bill);
        this.coverage = coverage;
    }

    @Override
    public double getCost() {
        return bill.getCost() - coverage;
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + " | Insurance coverage: -" + coverage;
    }
}

// Tax Decorator
class TaxDecorator extends BillDecorator {

    private double taxRate;

    public TaxDecorator(Bill bill, double taxRate) {
        super(bill);
        this.taxRate = taxRate;
    }

    @Override
    public double getCost() {
        return bill.getCost() + (bill.getCost() * taxRate);
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + " | Tax: " + (taxRate * 100) + "%";
    }
}

// Discount Decorator
class DiscountDecorator extends BillDecorator {

    private double discount;

    public DiscountDecorator(Bill bill, double discount) {
        super(bill);
        this.discount = discount;
    }

    @Override
    public double getCost() {
        return bill.getCost() - discount;
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + " | Discount: -" + discount;
    }
}

public class adminDashboard extends javax.swing.JFrame {

    public static HashMap<String, Integer> specializedMap = new HashMap();
    public static HashMap<String, Integer> roleMap = new HashMap();
    public static HashMap<String, Integer> billingMap = new HashMap();
    private StaffGroup allStaffGroup = new StaffGroup("All Staff");
    public static HashMap<String, Integer> ClaimMap = new HashMap();
    public static HashMap<String, Integer> IprovidersMap = new HashMap();

    /**
     * Creates new form adminDashboard
     */
    public adminDashboard() {
        initComponents();
        loadSpecialized();
        loadRole();
        loadStaff();
        loadPstatus();
        loadClaimedStatus();
        loadInsuranceProviders();
        loadBilling();
        loadInsuranceClaim();

    }

    private void loadSpecialized() {
        try {

            ResultSet resultSet = MySQL.execute("SELECT * FROM `specialization`");

            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("name"));
                specializedMap.put(resultSet.getString("name"), resultSet.getInt("id"));
            }

            DefaultComboBoxModel model1 = new DefaultComboBoxModel(v);

            jComboBox1.setModel(model1);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void loadRole() {
        try {

            ResultSet resultSet = MySQL.execute("SELECT * FROM `roles`");

            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("role_name"));
                roleMap.put(resultSet.getString("role_name"), resultSet.getInt("id"));
            }

            DefaultComboBoxModel model1 = new DefaultComboBoxModel(v);

            jComboBox2.setModel(model1);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void loadStaff() {
        try {
            String query = "SELECT staff.id, staff.full_name, staff.contact_number, staff.email, staff.password, "
                    + "specialization.name AS specialization_name, roles.role_name "
                    + "FROM staff "
                    + "INNER JOIN roles ON staff.roles_id = roles.id "
                    + "INNER JOIN specialization ON staff.specialization_id = specialization.id "
                    + "ORDER BY staff.id ASC";

            ResultSet resultset = MySQL.execute(query);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            // -------- Composite Pattern --------
            allStaffGroup = new StaffGroup("All Staff"); // reset before loading
            // -------- End Composite Pattern --------

            while (resultset.next()) {
                Vector<String> v = new Vector<>();

                String id = resultset.getString("id");
                String name = resultset.getString("full_name");
                String contact = resultset.getString("contact_number");
                String email = resultset.getString("email");
                String password = resultset.getString("password");
                String specialization = resultset.getString("specialization_name");
                String role = resultset.getString("role_name");

                v.add(id);
                v.add(name);
                v.add(contact);
                v.add(email);
                v.add(password);
                v.add(specialization);
                v.add(role);

                model.addRow(v);

                // -------- Composite Pattern --------
                StaffMember member = new StaffMember(name, role);
                allStaffGroup.add(member);

            }

            // -------- Composite Pattern (debug/demo) --------
            allStaffGroup.showDetails();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPstatus() {
        try {

            ResultSet resultSet = MySQL.execute("SELECT * FROM `payment_status`");

            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("staus"));
                billingMap.put(resultSet.getString("staus"), resultSet.getInt("id"));
            }

            DefaultComboBoxModel model1 = new DefaultComboBoxModel(v);

            jComboBox3.setModel(model1);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void loadInsuranceProviders() {
        try {

            ResultSet resultSet = MySQL.execute("SELECT * FROM `insurance_provider`");

            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("name"));
                IprovidersMap.put(resultSet.getString("name"), resultSet.getInt("id"));
            }

            DefaultComboBoxModel model1 = new DefaultComboBoxModel(v);

            jComboBox4.setModel(model1);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void loadClaimedStatus() {
        try {

            ResultSet resultSet = MySQL.execute("SELECT * FROM `claim_status`");

            Vector v = new Vector();
            v.add("Select");

            while (resultSet.next()) {
                v.add(resultSet.getString("status"));
                ClaimMap.put(resultSet.getString("status"), resultSet.getInt("id"));
            }

            DefaultComboBoxModel model1 = new DefaultComboBoxModel(v);

            jComboBox5.setModel(model1);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void loadBilling() {
        try {
            ResultSet resultset = MySQL.execute("SELECT billing.id AS billing_id, "
                    + "patient.name, patient.contact_number, "
                    + "billing.total, payment_status.staus AS payment_status "
                    + "FROM billing "
                    + "INNER JOIN patient ON billing.patient_id = patient.id "
                    + "INNER JOIN payment_status ON billing.payment_status_id = payment_status.id "
                    + "ORDER BY billing.id ASC");

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            while (resultset.next()) {
                Vector<String> v = new Vector<>();
                v.add(resultset.getString("billing_id"));
                v.add(resultset.getString("name"));
                v.add(resultset.getString("contact_number"));
                v.add(resultset.getString("total"));
                v.add(resultset.getString("payment_status"));

                model.addRow(v);
            }

            jTable2.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInsuranceClaim() {
        try {
            ResultSet resultset = MySQL.execute(
                    "SELECT insurance_claims.id AS insurance_claims_id, "
                    + "insurance_claims.processed_by, "
                    + "patient.name, "
                    + "insurance_provider.name AS insurance_provider_name, "
                    + "claim_status.status AS claim_status "
                    + "FROM insurance_claims "
                    + "INNER JOIN billing ON insurance_claims.billing_id = billing.id "
                    + "INNER JOIN patient ON billing.patient_id = patient.id "
                    + "INNER JOIN claim_status ON insurance_claims.claim_status_id = claim_status.id "
                    + "INNER JOIN insurance_provider ON insurance_claims.insurance_provider_id = insurance_provider.id "
                    + "ORDER BY insurance_claims.id ASC");

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);

            while (resultset.next()) {
                Vector<String> v = new Vector<>();
                v.add(resultset.getString("insurance_claims_id"));
                v.add(resultset.getString("processed_by"));
                v.add(resultset.getString("name"));
                v.add(resultset.getString("insurance_provider_name"));
                v.add(resultset.getString("claim_status"));

                model.addRow(v);
            }

            jTable3.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jPasswordField1.setText("");
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);

        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField9.setText("");
        jTextField10.setText("");
        jTextField11.setText("");

        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(0);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox<>();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(880, 550));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("ID");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Full Name");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Contact Number");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Email");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Password");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Specialized For");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Role");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("ADD STAFF");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("SEARCH STAFF");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("DELETE STAFF");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("UPDATE STAFF");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4)
                            .addComponent(jPasswordField1)
                            .addComponent(jComboBox1, 0, 126, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Full Name", "Contact Number", "Email", "Password", "Specialized For", "Role"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manage Staff", jPanel2);

        jLabel9.setText(" ID");

        jLabel10.setText("Name");

        jLabel11.setText("Contact Number");

        jLabel12.setText("Total");

        jLabel13.setText("Payment Status");

        jButton5.setText("ADD BILL");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("UPDATE BILL");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("SEARCH BILL");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton10.setText("GENERATE BILL");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 24, Short.MAX_VALUE))
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField11)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(15, 15, 15))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox3))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton10))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel14.setText("ID");

        jLabel15.setText("Processed by");

        jLabel16.setText("Patient Name");

        jLabel17.setText("Insurance Provider");

        jLabel18.setText("Claimed Status");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton8.setText("SEARCH");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("UPDATE");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField6)
                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16))
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17))
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Contact Number", "Total", "Payment Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setShowGrid(true);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
            jTable2.getColumnModel().getColumn(4).setResizable(false);
        }

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Processed By", "Patient Name", "Insurance Provider", "Claimed Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setResizable(false);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
            jTable3.getColumnModel().getColumn(2).setResizable(false);
            jTable3.getColumnModel().getColumn(3).setResizable(false);
            jTable3.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Billing and Insurance", jPanel3);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1010, 600));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 255));
        jLabel1.setText("Admin Dashboard- GlobeMed Hospital");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String fullname = jTextField2.getText();
        String contactNumber = jTextField3.getText();
        String email = jTextField4.getText();
        String password = String.valueOf(jPasswordField1.getPassword());
        String specialized = String.valueOf(jComboBox1.getSelectedItem()); // specialization name
        String role = String.valueOf(jComboBox2.getSelectedItem());        // role name

        if (fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Full Name", "Warning", JOptionPane.ERROR_MESSAGE);
        } else if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Insert Contact Number", "Warning", JOptionPane.ERROR_MESSAGE);
        } else if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Email", "Warning", JOptionPane.ERROR_MESSAGE);
        } else if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Password", "Warning", JOptionPane.ERROR_MESSAGE);
        } else if (role.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Invalid Role", "Warning", JOptionPane.ERROR_MESSAGE);
        } else if (specialized.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Invalid Specialization", "Warning", JOptionPane.ERROR_MESSAGE);
        } else {
            try {

                ResultSet rsSpec = MySQL.execute("SELECT id FROM specialization WHERE name = '" + specialized + "'");
                int specId = 0;
                if (rsSpec.next()) {
                    specId = rsSpec.getInt("id");
                }

                ResultSet rsRole = MySQL.execute("SELECT id FROM roles WHERE role_name = '" + role + "'");
                int roleId = 0;
                if (rsRole.next()) {
                    roleId = rsRole.getInt("id");
                }

                MySQL.execute("INSERT INTO `staff` (`full_name`, `contact_number`, `email`, `password`, `specialization_id`, `roles_id`) "
                        + "VALUES ('" + fullname + "', '" + contactNumber + "', '" + email + "', '" + password + "', '" + specId + "', '" + roleId + "')");

                loadStaff();

                JOptionPane.showMessageDialog(this, "Successful");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            System.out.println("Please select row");
        } else {

            String id = String.valueOf(jTable1.getValueAt(selectedRow, 0));

            try {

                MySQL.execute("DELETE FROM `staff` WHERE `id`='" + id + "'");
                JOptionPane.showMessageDialog(this, "Successful");
                loadStaff();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String id = jTextField1.getText();
        String fullname = jTextField2.getText();
        String contactNumber = jTextField3.getText();
        String email = jTextField4.getText();
        String password = String.valueOf(jPasswordField1.getPassword());
        String specialized = String.valueOf(jComboBox1.getSelectedItem());
        String role = String.valueOf(jComboBox2.getSelectedItem());

        String query = "SELECT staff.id, staff.full_name, staff.contact_number, staff.email, staff.password, "
                + "specialization.name AS specialization_name, roles.role_name "
                + "FROM staff "
                + "INNER JOIN roles ON staff.roles_id = roles.id "
                + "INNER JOIN specialization ON staff.specialization_id = specialization.id "
                + "WHERE staff.id = '" + Integer.parseInt(id) + "' ";

        try {

            ResultSet rs = MySQL.execute(query);

            if (rs.next()) {

                jTextField1.setText(rs.getString("id"));
                jTextField2.setText(rs.getString("full_name"));
                jTextField3.setText(rs.getString("contact_number"));
                jTextField4.setText(rs.getString("email"));
                jPasswordField1.setText(rs.getString("password"));

                jComboBox1.setSelectedItem(rs.getString("specialization_name"));
                jComboBox2.setSelectedItem(rs.getString("role_name"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:

        if (evt.getClickCount() == 2) {

            int selectedRow = jTable1.getSelectedRow();

            String id = String.valueOf(jTable1.getValueAt(selectedRow, 0));
            jTextField1.setText(id);

            String full_name = String.valueOf(jTable1.getValueAt(selectedRow, 1));
            jTextField2.setText(full_name);

            String contact = String.valueOf(jTable1.getValueAt(selectedRow, 2));
            jTextField3.setText(contact);

            String email = String.valueOf(jTable1.getValueAt(selectedRow, 3));
            jTextField4.setText(email);

            String password = String.valueOf(jTable1.getValueAt(selectedRow, 4));
            jPasswordField1.setText(password);

            String specialized = String.valueOf(jTable1.getValueAt(selectedRow, 5));
            jComboBox1.setSelectedItem(specialized);

            String role = String.valueOf(jTable1.getValueAt(selectedRow, 6));
            jComboBox2.setSelectedItem(role);

        }

    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:

        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            String id = String.valueOf(jTable1.getValueAt(selectedRow, 0));

            String fullname = jTextField2.getText().trim();
            String contactNumber = jTextField3.getText().trim();
            String email = jTextField4.getText().trim();
            String password = String.valueOf(jPasswordField1.getPassword()).trim();
            String specializationName = String.valueOf(jComboBox1.getSelectedItem());
            String roleName = String.valueOf(jComboBox2.getSelectedItem());

            // Input validation
            if (fullname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter full name", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (contactNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid contact number", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid email", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (specializationName.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a specialization", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (roleName.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a role", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {

                    ResultSet specRs = MySQL.execute("SELECT id FROM specialization WHERE name = '" + specializationName + "'");
                    int specializationId = -1;
                    if (specRs.next()) {
                        specializationId = specRs.getInt("id");
                    } else {
                        JOptionPane.showMessageDialog(this, "Specialization not found", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ResultSet roleRs = MySQL.execute("SELECT id FROM roles WHERE role_name = '" + roleName + "'");
                    int roleId = -1;
                    if (roleRs.next()) {
                        roleId = roleRs.getInt("id");
                    } else {
                        JOptionPane.showMessageDialog(this, "Role not found", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    MySQL.execute("UPDATE staff SET "
                            + "full_name = '" + fullname + "', "
                            + "contact_number = '" + contactNumber + "', "
                            + "email = '" + email + "', "
                            + "password = '" + password + "', "
                            + "specialization_id = " + specializationId + ", "
                            + "roles_id = " + roleId + " "
                            + "WHERE id = " + id);

                    JOptionPane.showMessageDialog(this, "Staff member updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStaff();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error while updating staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        String id = jTextField5.getText().trim();
        String name = jTextField9.getText().trim();
        String contact = jTextField10.getText().trim();
        String total = jTextField11.getText().trim();
        String paymentStatus = String.valueOf(jComboBox3.getSelectedItem());
        String insuranceProvider = String.valueOf(jComboBox4.getSelectedItem());

        if (name.isEmpty() || contact.isEmpty() || total.isEmpty() || paymentStatus.equalsIgnoreCase("Select")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly", "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double baseAmount = Double.parseDouble(total);

            // ---------- Using Decorator Pattern ----------
            Bill bill = new BaseBill(baseAmount);

            // Apply insurance if provider selected
            if (!insuranceProvider.equalsIgnoreCase("Select")) {
                bill = new InsuranceDecorator(bill, 500.0); // fixed demo coverage
            }

            // Apply tax (5%)
            bill = new TaxDecorator(bill, 0.05);

            // Apply discount 
            bill = new DiscountDecorator(bill, 100.0);

            double finalAmount = bill.getCost();
            String description = bill.getDescription();
            // ------------------------------------------------

            //  Get payment_status_id
            Integer payment_status_id = billingMap.get(paymentStatus);
            if (payment_status_id == null) {
                throw new Exception("Invalid Payment Status selected");
            }

            String insertPatient = "INSERT INTO patient (`id`, `name`, `dob`, `contact_number`, `address`, `city_id`, `gender_id`, `diagnose`) VALUES ("
                    + "'" + id + "', "
                    + "'" + name + "', "
                    + "NULL, "
                    + "'" + contact + "', "
                    + "NULL, "
                    + "2, "
                    + "NULL, "
                    + "NULL)";
            MySQL.execute(insertPatient);

            // Insert into billing with final decorated amount
            String insertBilling = "INSERT INTO billing (`id`, `total`, `created_at`, `patient_id`, `payment_status_id`) VALUES ("
                    + "'" + id + "', "
                    + "'" + finalAmount + "', "
                    + "CURRENT_TIMESTAMP, "
                    + "'" + id + "', "
                    + "'" + payment_status_id + "')";
            MySQL.execute(insertBilling);

            loadBilling();
            clearFields();
            JOptionPane.showMessageDialog(this, "Bill Generated!\n" + description);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:

        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = jTextField5.getText().trim();
        String name = jTextField9.getText().trim();
        String contact = jTextField10.getText().trim();
        String total = jTextField11.getText().trim();
        String paymentStatus = String.valueOf(jComboBox3.getSelectedItem());
        String insuranceProvider = String.valueOf(jComboBox4.getSelectedItem());

        if (name.isEmpty() || contact.isEmpty() || total.isEmpty() || paymentStatus.equalsIgnoreCase("Select")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly", "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double baseAmount = Double.parseDouble(total);

            // ---------- Using Decorator Pattern ----------
            Bill bill = new BaseBill(baseAmount);

            if (!insuranceProvider.equalsIgnoreCase("Select")) {
                bill = new InsuranceDecorator(bill, 500.0);
            }
            bill = new TaxDecorator(bill, 0.05);
            bill = new DiscountDecorator(bill, 100.0);

            double finalAmount = bill.getCost();
            String description = bill.getDescription();
            // ------------------------------------------------

            Integer payment_status_id = billingMap.get(paymentStatus);
            if (payment_status_id == null) {
                JOptionPane.showMessageDialog(this, "Invalid Payment Status ID", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updateQuery = "UPDATE `billing` SET "
                    + "`total`='" + finalAmount + "', "
                    + "`payment_status_id`='" + payment_status_id + "', "
                    + "`created_at`=CURRENT_TIMESTAMP "
                    + "WHERE `id`='" + id + "'";
            MySQL.execute(updateQuery);

            loadBilling();
            clearFields();
            JOptionPane.showMessageDialog(this, "Bill Updated!\n" + description);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String id = jTextField5.getText().trim();

        if (id.isEmpty() || !id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid ID", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT billing.id AS billing_id, "
                + "patient.name, patient.contact_number, "
                + "billing.total, payment_status.staus AS payment_status "
                + "FROM billing "
                + "INNER JOIN patient ON billing.patient_id = patient.id "
                + "INNER JOIN payment_status ON billing.payment_status_id = payment_status.id "
                + "WHERE billing.id = " + id;

        try {
            ResultSet rs = MySQL.execute(query);

            if (rs.next()) {
                jTextField9.setText(rs.getString("name"));
                jTextField10.setText(rs.getString("contact_number"));
                jTextField11.setText(rs.getString("total"));
                jComboBox3.setSelectedItem(rs.getString("payment_status"));
            } else {
                JOptionPane.showMessageDialog(this, "No record found with ID: " + id, "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        int selectedRow = jTable3.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Row", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = jTextField6.getText().trim();
        String processed_by = jTextField7.getText().trim();
        String ClaimStatus = String.valueOf(jComboBox5.getSelectedItem());

        if (ClaimStatus.equalsIgnoreCase("Select")) {
            JOptionPane.showMessageDialog(this, "Invalid Claim Status", "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer claim_status_id = ClaimMap.get(ClaimStatus);
        if (claim_status_id == null) {
            JOptionPane.showMessageDialog(this, "Invalid Claim Status", "Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (id.isEmpty() || !id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid ID", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ---------- Decorator Pattern applied ----------
            // Fetch billing total for this claim
            String billingQuery = "SELECT total FROM billing WHERE id=" + id;
            ResultSet rs = MySQL.execute(billingQuery);

            double baseAmount = 0;
            if (rs.next()) {
                baseAmount = rs.getDouble("total");
            }

            Bill bill = new BaseBill(baseAmount);

            // Apply insurance if provider chosen
            if (!String.valueOf(jComboBox4.getSelectedItem()).equalsIgnoreCase("Select")) {
                bill = new InsuranceDecorator(bill, 500.0);
            }

            // Always apply tax
            bill = new TaxDecorator(bill, 0.05);

            // If claim approved → discount
            if ("Approved".equalsIgnoreCase(ClaimStatus)) {
                bill = new DiscountDecorator(bill, 100.0);
            }

            double finalAmount = bill.getCost();
            String desc = bill.getDescription();
            // ------------------------------------------------

            // Update insurance_claims table
            String updateQuery = "UPDATE `insurance_claims` SET "
                    + "`processed_by`='" + processed_by + "', "
                    + "`claim_status_id`=" + claim_status_id + " "
                    + "WHERE `id`=" + id;

            MySQL.execute(updateQuery);

            loadInsuranceClaim();

            JOptionPane.showMessageDialog(this,
                    "Insurance Claim Updated!\n" + desc + "\nFinal Amount: " + finalAmount);
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while updating", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        try {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("invoice", 12);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/globemed", "root", "jungkook123@A");

            JasperPrint report = JasperFillManager.fillReport("src/report/billing.jasper", parameters, connection);
            JasperViewer.viewReport(report, false);

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:

        if (evt.getClickCount() == 2) {
            int selectedRow = jTable2.getSelectedRow();

            String id = String.valueOf(jTable2.getValueAt(selectedRow, 0));
            jTextField5.setText(id);

            String Name = String.valueOf(jTable2.getValueAt(selectedRow, 1));
            jTextField9.setText(Name);

            String contact = String.valueOf(jTable2.getValueAt(selectedRow, 2));
            jTextField10.setText(contact);

            String total = String.valueOf(jTable2.getValueAt(selectedRow, 3));
            jTextField11.setText(total);

            String payment_status = String.valueOf(jTable2.getValueAt(selectedRow, 4));
            jComboBox3.setSelectedItem(payment_status);

        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        String id = jTextField6.getText().trim();

        if (id.isEmpty() || !id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid ID", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT insurance_claims.id AS insurance_claims_id, "
                + "insurance_claims.processed_by, "
                + "patient.name, "
                + "insurance_provider.name AS insurance_provider_name, "
                + "claim_status.status AS claim_status "
                + "FROM insurance_claims "
                + "INNER JOIN billing ON insurance_claims.billing_id = billing.id "
                + "INNER JOIN patient ON billing.patient_id = patient.id "
                + "INNER JOIN claim_status ON insurance_claims.claim_status_id = claim_status.id "
                + "INNER JOIN insurance_provider ON insurance_claims.insurance_provider_id = insurance_provider.id "
                + "WHERE billing.id = " + id;

        try {
            ResultSet rs = MySQL.execute(query);

            if (rs.next()) {
                jTextField7.setText(rs.getString("processed_by"));
                jTextField8.setText(rs.getString("patient.name"));
                jComboBox4.setSelectedItem(rs.getString("insurance_provider_name")); // this is what you aliased
                jComboBox5.setSelectedItem(rs.getString("claim_status"));
            } else {
                JOptionPane.showMessageDialog(this, "No record found with ID: " + id, "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int selectedRow = jTable3.getSelectedRow();

            String id = String.valueOf(jTable3.getValueAt(selectedRow, 0));
            jTextField6.setText(id);

            String processed_by = String.valueOf(jTable3.getValueAt(selectedRow, 1));
            jTextField7.setText(processed_by);

            String patient_name = String.valueOf(jTable3.getValueAt(selectedRow, 2));
            jTextField8.setText(patient_name);

            String provider = String.valueOf(jTable3.getValueAt(selectedRow, 3));
            jComboBox4.setSelectedItem(provider);

            String claim_status = String.valueOf(jTable3.getValueAt(selectedRow, 4));
            jComboBox5.setSelectedItem(claim_status);

        }
    }//GEN-LAST:event_jTable3MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatCyanLightIJTheme.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
