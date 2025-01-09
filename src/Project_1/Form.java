package Project_1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Form extends JFrame implements ActionListener {

    JTextField nameField;
    JTextField priceField;
    JTextField quantityField;
    JButton createButton;
    JButton updateButton;
    JButton deleteButton;
    JTable table;
    DefaultTableModel tableModle;

    public Form(){

        this.setTitle("CRUD Frame");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000,300);
        ImageIcon image = new ImageIcon("portfolio.png");
        this.setIconImage(image.getImage());
        this.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2,2,0,0));

        JPanel gridPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gridPanel.add(new JLabel("Name: "), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        gridPanel.add(nameField, gbc);

        panel.add(gridPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gridPanel.add(new JLabel("Price: "), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(15);
        gridPanel.add(priceField, gbc);

        panel.add(gridPanel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gridPanel.add(new JLabel("Quantity: "), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(15);
        gridPanel.add(quantityField, gbc);

        panel.add(gridPanel);


        JPanel panletable = new JPanel();
        tableModle = new DefaultTableModel(new String[]{"Name", "Price", "Quantity"}, 0);
        table = new JTable(tableModle);
        JScrollPane scrollPane = new JScrollPane(table);
        panletable.add(scrollPane);

        panel.add(panletable);



        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        createButton = new JButton("Create");
        createButton.addActionListener(this);
        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel);

        this.add(panel, BorderLayout.WEST);
        this.setVisible(true);

        loadData();
    }

    private void loadData(){
        String url = "jdbc:mysql://localhost:3306/formta";
        String user = "root";
        String password = "20040202";
        String query = "SELECT name, price, quantity FROM users";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,user,password);
            Statement statement = connection.createStatement();
            ResultSet resultobj = statement.executeQuery(query);

            tableModle.setRowCount(0);

            String name;
            int price;
            int quantity;

            while (resultobj.next()){
                name = resultobj.getString("name");
                price = resultobj.getInt("price");
                quantity = resultobj.getInt("quantity");
                tableModle.addRow(new Object[]{name, price, quantity});
            }
            connection.close();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==createButton) {

            String url = "jdbc:mysql://localhost:3306/formta";
            String user = "root";
            String password = "20040202";
            String name = nameField.getText();
            String price = priceField.getText();
            String quantity = quantityField.getText();
            String query = "INSERT INTO users (name, price, quantity) VALUES(?, ?, ?)";
            

            if(name.isEmpty()||price.isEmpty()||quantity.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url,user,password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, price);
                preparedStatement.setString(3, quantity);

                int rowsInserted = preparedStatement.executeUpdate();

                if(rowsInserted > 0) {
                    System.out.println("Name added to the database successfully!");

                    tableModle.addRow(new Object[]{name, price, quantity});
                }

                connection.close();
            }
            catch (Exception error){
                JOptionPane.showMessageDialog(this, "Error: " + error.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource()==deleteButton) {

            int selectedRow = table.getSelectedRow();

            String url = "jdbc:mysql://localhost:3306/formta";
            String user = "root";
            String password = "20040202";
            String name = tableModle.getValueAt(selectedRow, 0).toString();
            String query = "DELETE FROM users WHERE name = ?";

            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url,user,password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);

                preparedStatement.setString(1, name);

                int rowsDeleted = preparedStatement.executeUpdate();

                if(rowsDeleted > 0) {
                    System.out.println("Name deleted to the database successfully!");

                    tableModle.removeRow(selectedRow);
                }
                connection.close();

            }
            catch (Exception error1){
                System.out.println("Somethins went wrong" + error1);
            }
        }
        else if (e.getSource()==updateButton){

            int selectedRow = table.getSelectedRow();

            String url = "jdbc:mysql://localhost:3306/formta";
            String user = "root";
            String password = "20040202";
            String name = nameField.getText();
            String price = priceField.getText();
            String quantity = quantityField.getText();
            String query = "UPDATE users SET name = ?, price = ?, quantity = ? WHERE name = ?";

            if(name.isEmpty()||price.isEmpty()||quantity.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String originalName = tableModle.getValueAt(selectedRow,0).toString();

            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url,user,password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);

                preparedStatement.setString(1,name);
                preparedStatement.setString(2,price);
                preparedStatement.setString(3,quantity);
                preparedStatement.setString(4,originalName);

                int rowsUpdate = preparedStatement.executeUpdate();

                if (rowsUpdate > 0){
                    JOptionPane.showMessageDialog(this, "Data updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    tableModle.setValueAt(name,selectedRow,0);
                    tableModle.setValueAt(price,selectedRow, 1);
                    tableModle.setValueAt(quantity,selectedRow, 2);

                    nameField.setText("");
                    priceField.setText("");
                    quantityField.setText("");
                }



            }
            catch (Exception error3){
                System.out.println("Something went wrong" + error3);
            }

        }
    }
}
