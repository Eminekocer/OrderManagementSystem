package view;

import bussiness.BasketController;
import bussiness.CartController;
import bussiness.CustomerController;
import bussiness.ProductController;
import core.Helper;
import core.Item;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane tab_menu;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox cmb_f_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private JButton btn_customer_new;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_product_name;
    private JTextField fld_product_code;
    private JComboBox<Item> cmb_product_stock;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton bt_product_filter_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stock;
    private JPanel pnl_basket;
    private JPanel pnl_basket_top;
    private JScrollPane scrl_basket;
    private JLabel lbl_;
    private JComboBox<Item> cmb_basket_customer;
    private JButton btn_basket_reset;
    private JLabel lbl_basket_price;
    private JLabel lbl_basket_count;
    private JButton btn_basket_new;
    private JTable tbl_basket;
    private JScrollPane scrl_cart;
    private JTable tbl_cart;
    private User user;
    private BasketController basketController;
    private CustomerController customerController;
    private ProductController productController;
    private CartController cartController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private DefaultTableModel tmdl_basket= new DefaultTableModel();
    private DefaultTableModel tmdl_cart = new DefaultTableModel();


    private JPopupMenu popup_customer = new JPopupMenu();
    private DefaultTableModel tmdl_product = new DefaultTableModel();
    private  JPopupMenu popup_product = new JPopupMenu();

    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController=new BasketController();
        this.cartController= new CartController();


        if (user == null) {
            Helper.showMsg("error");
            dispose();
        }
        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(1000, 500);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

        this.setLocation(x, y);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldin: " + this.user.getName());


        this.btn_logout.addActionListener(e -> {
            dispose();
            LoginUI loginUI = new LoginUI();
        });
        //CUSTOMER TAB
        loadCustomerTable(null);
        loadCustomerPopupMenu();
        loadCustomerButtonEvent();
        this.cmb_f_customer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_f_customer_type.setSelectedItem(null);

        //PRODUCT TAB
        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_product_stock.addItem(new Item(1, "Stokta Var"));
        this.cmb_product_stock.addItem(new Item(2, "Stokta Yok"));
        this.cmb_product_stock.setSelectedItem(null);

        //BASKET TABB
        loadBasketTable();
        loadBasketButtonEvent();
        loadBasketCustomerCombo();

        //CART TAB
        loadCartTable();
    }

    private void loadCartTable(){
        Object[] columnCart = {"Id","Müşteri Adı","Ürün Adı","Fiyat","Sipariş Tarihi","Not"};
        ArrayList<Cart> carts = this.cartController.findAll();
        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_cart.getModel();
        clearModel.setRowCount(0);
        this.tmdl_cart.setColumnIdentifiers(columnCart);
        int totalprice=0;
        for(Cart cart : carts){
            Object[] rowObject = {
                    cart.getId(),
                    cart.getCustomer().getName(),
                    cart.getProduct().getName(),
                    cart.getPrice(),
                    cart.getDate(),
                    cart.getNote()
            };
            this.tmdl_cart.addRow(rowObject);
            totalprice += cart.getProduct().getPrice();
        }

        this.tbl_cart.setModel(tmdl_cart);
        this.tbl_cart.getTableHeader().setReorderingAllowed(false);
        this.tbl_cart.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_cart.setEnabled(false);
    }

    private void loadBasketCustomerCombo(){
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_basket_customer.removeAllItems();
        for(Customer customer : customers){
            int comboKey = customer.getId();
            String comboValue = customer.getName();


            this.cmb_basket_customer.addItem(new Item(comboKey,comboValue));
        }
        this.cmb_basket_customer.setSelectedItem(null);
    }
    private void loadBasketButtonEvent(){
        this.btn_basket_reset.addActionListener(e -> {
            if(this.basketController.clear()){
                Helper.showMsg("done");
                loadBasketTable();
            } else{
                Helper.showMsg("error");
            }

        });
        this.btn_basket_new.addActionListener(e -> {
            Item selectedCustomer =(Item) this.cmb_basket_customer.getSelectedItem();

            if(selectedCustomer == null){
                Helper.showMsg("Lütfen Bir Müşteri Seçiniz!");

            }else{
                Customer customer = this.customerController.getById(selectedCustomer.getKey());
                ArrayList<Basket> baskets =this.basketController.findAll();
                if(customer.getId() == 0){
                    Helper.showMsg("Böyle bir müşteri bulunamadı!");
                }else if (baskets.size() == 0){
                    Helper.showMsg("Lütfen Sepete Ürün Ekleyiniz!");
                }else {
                    CartUI cartUI=new CartUI(customer);
                    cartUI.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            loadBasketTable();
                            loadProductTable(null);
                        }
                    });
                }
            }


        });
    }
    private  void loadBasketTable(){
        Object[] columnBasket = {"Id","Ürün Adı","Ürün Kodu","Fiyat","Stok"};
        ArrayList<Basket> baskets = this.basketController.findAll();
        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_basket.getModel();
        clearModel.setRowCount(0);
        this.tmdl_basket.setColumnIdentifiers(columnBasket);
        int totalprice=0;
        for(Basket basket : baskets){
            Object[] rowObject = {
                    basket.getId(),
                    basket.getProduct().getName(),
                    basket.getProduct().getCode(),
                    basket.getProduct().getPrice(),
                    basket.getProduct().getStock()
            };
            this.tmdl_basket.addRow(rowObject);
            totalprice += basket.getProduct().getPrice();
        }
        this.lbl_basket_price.setText(totalprice + " TL");
        this.lbl_basket_count.setText(baskets.size()+ " Adet");

        this.tbl_basket.setModel(tmdl_basket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_basket.setEnabled(false);
    }
    private void loadProductButtonEvent(){
        this.bt_product_filter_new.addActionListener(e -> {
        ProductUI productUI = new ProductUI(new Product());
        productUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
               loadProductTable(null);
            }
        });
        });
        btn_product_filter.addActionListener(e -> {
        ArrayList<Product> filteredProducts= this.productController.filter(
                this.fld_product_name.getText(),
                this.fld_product_code.getText(),
                (Item) this.cmb_product_stock.getSelectedItem()
        );
        loadProductTable(filteredProducts);
        });
        btn_product_filter_reset.addActionListener(e -> {
            this.fld_product_code.setText(null);
            this.fld_product_name.setText(null);
            this.cmb_product_stock.setSelectedItem(null);
            loadProductTable(null);
        });
    }
    private  void loadProductPopupMenu(){
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });
        this.popup_product.add("Sepete Ekle").addActionListener(e -> {
            int selectId=Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            Product basketproduct = this.productController.getById(selectId);
            if(basketproduct.getStock() <= 0){
                Helper.showMsg("Bu ürün stokta yoktur.");
            }else{
                Basket basket = new Basket(basketproduct.getId());

                if(this.basketController.save(basket)){
                    Helper.showMsg("done");
                    loadBasketTable();
                }else{
                    Helper.showMsg("error");
                }

            }
        });
        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectId=Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectId));
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                 loadProductTable(null);
                 loadBasketTable();

                }
            });
        });

        this.popup_product.add("Sil").addActionListener(e ->{
            int selectId=Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            if(Helper.confirm("sure")){
            if(this.productController.delete(selectId)){
                Helper.showMsg("done");
                loadProductTable(null);
                loadBasketTable();

            } else{
            Helper.showMsg("error");}
            }
                });

        this.tbl_product.setComponentPopupMenu(this.popup_product);

    }
    private  void loadProductTable(ArrayList<Product> products){
        Object[] columnProduct = {"Id","Ürün Adı","Ürün Kodu","Fiyat","Stok"};
        if(products == null){
            products = this.productController.findAll();
        }
        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel();
        clearModel.setRowCount(0);
        this.tmdl_product.setColumnIdentifiers(columnProduct);
        for(Product product : products){
            Object[] rowObject = {product.getId(),
                    product.getName(),
                    product.getCode(),
                    product.getPrice(),
                    product.getStock()
            };
            this.tmdl_product.addRow(rowObject);
        }
        this.tbl_product.setModel(tmdl_product);
        this.tbl_product.getTableHeader().setReorderingAllowed(false);
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_product.setEnabled(false);
    }
    private void loadCustomerButtonEvent(){
        this.btn_customer_new.addActionListener(e -> {
        CustomerUI customerUI= new CustomerUI(new Customer());
        customerUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
               loadCustomerTable(null);
                loadBasketCustomerCombo();
            }
        });
        });
        this.btn_customer_filter.addActionListener(e -> {
            ArrayList<Customer> filteredCustomers = this.customerController.filter(
                    this.fld_f_customer_name.getText(),
                    (Customer.TYPE) this.cmb_f_customer_type.getSelectedItem()
            );
            loadCustomerTable(filteredCustomers);

        });
        this.btn_customer_filter_reset.addActionListener(e -> {
            loadCustomerTable(null);
            this.fld_f_customer_name.setText(null);
            this.cmb_f_customer_type.setSelectedItem(null);

        });
    }
    private  void  loadCustomerPopupMenu(){
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              int selectedRow = tbl_customer.rowAtPoint(e.getPoint());
              tbl_customer.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });
        this.popup_customer.add("Güncelle").addActionListener(e -> {
           int selectId=Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(),0).toString());
            CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId));
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }
            });


        });
        this.popup_customer.add("Sil").addActionListener(e -> {
            int selectId=Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(),0).toString());
            if(Helper.confirm("sure")){
                if(this.customerController.delete(selectId)){
                    Helper.showMsg("done");
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }else {
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
    }
    private  void loadCustomerTable(ArrayList<Customer> customers){
        Object[] columnCustomer = {"Id","Müşteri Adı","Tipi","Telefon","Eposta","Adres"};
        if(customers == null){
           customers = this.customerController.findAll();
        }
        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();
        clearModel.setRowCount(0);
        this.tmdl_customer.setColumnIdentifiers(columnCustomer);
        for(Customer customer : customers){
            Object[] rowObject = {customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress()
            };
            this.tmdl_customer.addRow(rowObject);
        }
        this.tbl_customer.setModel(tmdl_customer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_customer.setEnabled(false);
    }
}