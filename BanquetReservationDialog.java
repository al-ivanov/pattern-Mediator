import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class BanquetReservationDialog extends JFrame {
    private int peopleCount = PEOPLE_COUNT_DEFAULT;
    private Date date = null;
    private Date startTime = null;
    private Date endTime = null;
    private int serviceType = UNKNOWN_SERVICE;

    public static final int UNKNOWN_SERVICE = 0;
    public static final int BANQUET_SERVICE = 1;
    public static final int TABLE_SERVICE = 2;
    public final static int MIN_PEOPLE = 10;
    public final static int MAX_PEOPLE = 50;
    public final static int PEOPLE_COUNT_DEFAULT = 0;
    
    private JTextField countField;
    private JTextField dateField;
    private JTextField startField;
    private JTextField endField;

    BanquetReservationDialog() {
        super("Резервирование банкетного зала");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        BanquetMediator mediator = new BanquetMediator();
        Container contentPane = getContentPane();
        contentPane.add(createDispositionPanel(mediator),
                        BorderLayout.SOUTH);
        contentPane.add(createBodyPanel(mediator), BorderLayout.CENTER);
        contentPane.add(createTopPanel(mediator), BorderLayout.NORTH);
		setVisible(true);
        pack();
    } // constructor(Frame)

    private JPanel createDispositionPanel(BanquetMediator mediator) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new OkClick());
        mediator.registerOkButton(okButton);
        p.add(okButton);
        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(new cancelButtonClick());
        p.add(cancelButton);
        return p;
    } // createDispositionPanel()

    // create top panel
    private JPanel createTopPanel(BanquetMediator mediator) {
        JPanel top = new JPanel(new BorderLayout(10, 5));
        JPanel countPanel;
        countPanel = new JPanel();
        countPanel.add(new JLabel("Количество человек(10-50):"));
        countField = new JTextField(4);
        mediator.registerPeopleCountField(countField);
        countPanel.add(countField);
        top.add(countPanel, BorderLayout.WEST);

        return top;
    } // createTopPanel()

    private JPanel createBodyPanel(BanquetMediator mediator) {
        JPanel bodyPanel = new JPanel(new BorderLayout(5,5));
        bodyPanel.add(new JSeparator(), BorderLayout.NORTH);
        bodyPanel.add(createMainPanel(mediator));
        bodyPanel.add(new JSeparator(), BorderLayout.SOUTH);
        return bodyPanel;
    } // createBodyPanel()

    private Container createMainPanel(BanquetMediator mediator) {
        JPanel mainPanel;
        mainPanel= new JPanel(new BorderLayout(5,3));
        mainPanel.add(createDateTimePanel(mediator), BorderLayout.WEST);
        mainPanel.add(createServicePanel(mediator), BorderLayout.CENTER);
        String foods[]= { "Жареная говядина", "Вареные яйца", "Люля кебаб",
                          "Буррито", "Лазанья", "Ветчина", "Стейк",
                          "Шашлык из свинины", "Бефстроганов",
                          "Жареный цыпленок"};
        JList foodList = new JList(foods);
        int mode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        foodList.setSelectionMode(mode);
        foodList.setVisibleRowCount(7);
        mediator.registerFoodList(foodList);
        mainPanel.add(foodList, BorderLayout.EAST);

        return mainPanel;
    } // createMainPanel()

    private Container createDateTimePanel(BanquetMediator mediator) {
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        labelConstraints.insets = new Insets(5,0,0,0);
        labelConstraints.weighty = 0;
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.anchor = GridBagConstraints.NORTHWEST;
        fieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fieldConstraints.insets = new Insets(5,3,0,0);
        fieldConstraints.weighty = 0;
        JPanel datePanel = new JPanel(gb);
        JLabel dateLabel = new JLabel("Дата (MM/DD/YY):");
        gb.setConstraints(dateLabel, labelConstraints);
        datePanel.add(dateLabel);
        dateField = new JTextField(10);
        mediator.registerDateField(dateField);
        gb.setConstraints(dateField, fieldConstraints);
        datePanel.add(dateField);
        JLabel startLabel = new JLabel("Время начала (HH:MM):");
        gb.setConstraints(startLabel, labelConstraints);
        datePanel.add(startLabel);
        startField = new JTextField(7);
        mediator.registerStartField(startField);
        gb.setConstraints(startField, fieldConstraints);
        datePanel.add(startField);
        JLabel endLabel = new JLabel("Время конца (HH:MM):");
        labelConstraints.weighty = 1;
        gb.setConstraints(endLabel, labelConstraints);
        datePanel.add(endLabel);
        endField = new JTextField(7);
        mediator.registerEndField(endField);
        fieldConstraints.weighty = 1;
        gb.setConstraints(endField, fieldConstraints);
        datePanel.add(endField);
        return datePanel;
    } // createDateTimePanel()

    private Container createServicePanel(BanquetMediator mediator) {
        ButtonGroup serviceGroup = new ButtonGroup();
        JRadioButton tableServiceButton = new JRadioButton("Обслуживание");
        mediator.registerTableButton(tableServiceButton);
        serviceGroup.add(tableServiceButton);
        JRadioButton buffetButton = new JRadioButton("Шведский стол");
        mediator.registerBuffetButton(buffetButton);
        serviceGroup.add(buffetButton);

        JPanel servicePanel = new JPanel();
        servicePanel.setLayout(new BoxLayout(servicePanel, BoxLayout.Y_AXIS));
        servicePanel.setBorder(BorderFactory.createTitledBorder("Вид обслуживания"));
        servicePanel.add(tableServiceButton);
        servicePanel.add(buffetButton);
        JPanel pad = new JPanel();
        pad.add(servicePanel);
        return pad;
    } // createServicePanel


    public int getPeopleCount() { return peopleCount; }


    private Calendar getStartCalendar() {
        if (startTime == null || date == null)
          return null;
        return new GregorianCalendar(date.getYear(),
                                     date.getMonth(),
                                     date.getDate(),
                                     startTime.getHours(),
                                     startTime.getMinutes(),
                                     0);
    } // getStartCalendar()


    public Date getStartTime() {
        Calendar cal = getStartCalendar();
        return (cal == null) ? null : cal.getTime();
    } // getStartTime()


    private Calendar getEndCalendar() {
        if (endTime == null || date == null)
          return null;
        return new GregorianCalendar(date.getYear(),
                                     date.getMonth(),
                                     date.getDate(),
                                     endTime.getHours(),
                                     endTime.getMinutes(),
                                     0);
    } // getEndCalendar()


    public Date getEndTime() {
        Calendar cal = getEndCalendar();
        return (cal == null) ? null : cal.getTime();
    } // getEndTime()


    public int getServiceType() { return serviceType; }

    private class BanquetMediator {
        private JTextComponent peopleCountField;
        private JButton okButton;
        private JTextComponent dateField;
        private JTextComponent startField;
        private JTextComponent endField;
        private JToggleButton buffetButton;
        private JToggleButton tableServiceButton;
        private JList foodList;

        private ItemAdapter itemAdapter = new ItemAdapter();
        private ListSelectionAdapter listSelectionAdapter = new ListSelectionAdapter();

        private boolean busy = false;

        BanquetMediator() {
            WindowAdapter windowAdapter = new WindowAdapter() {

                public void windowOpened(WindowEvent e) {
                    initialState();
                } // windowOpened(WindowEvent)
              };
            BanquetReservationDialog.this.addWindowListener(windowAdapter);
        } // Constructor()


        private class ListSelectionAdapter implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                enforceInvariants();
            } // valueChanged(ListSelectionEvent)
        } // class ListSelectionAdapter


        private class ItemAdapter implements ItemListener {
        	
            public void itemStateChanged(ItemEvent e) {
                enforceInvariants();
            } // itemStateChanged(ItemEvent)
        } // class ItemAdapter


        private abstract class DocumentAdapter implements DocumentListener {

            public void insertUpdate(DocumentEvent e) {
                parseDocument();
                enforceInvariants();
            } // insertUpdate(DocumentEvent)

            public void removeUpdate(DocumentEvent e) {
                parseDocument();
                enforceInvariants();
            } // removeUpdate(DocumentEvent)

            public void changedUpdate(DocumentEvent e) {
                parseDocument();
                enforceInvariants();
            } // changedUpdate(DocumentEvent)


            protected abstract void parseDocument();
        } // class DocumentAdapter


        private void initialState() {
            peopleCount = PEOPLE_COUNT_DEFAULT;
            startTime = null;
            endTime = null;
            serviceType = UNKNOWN_SERVICE;
            peopleCountField.setText("");
            peopleCountField.setEnabled(true);
            dateField.setText("");
            dateField.setEnabled(false);
            startField.setText("");
            startField.setEnabled(false);
            endField.setText("");
            endField.setEnabled(false);
            tableServiceButton.setSelected(false);
            tableServiceButton.setEnabled(false);
            buffetButton.setSelected(false);
            buffetButton.setEnabled(false);
            foodList.clearSelection();
            foodList.setEnabled(false);
            okButton.setEnabled(false);
        } // initialState()

        private void enforceInvariants() {

            if (busy)
              return;
            busy = true;
            protectedEnforceInvariants();
            busy = false;
        } // enforceInvariants()

        private void protectedEnforceInvariants() {
            boolean enable = (peopleCount !=  PEOPLE_COUNT_DEFAULT);

            dateField.setEnabled(enable);
            startField.setEnabled(enable);
            endField.setEnabled(enable);
            buffetButton.setEnabled(enable);

            tableServiceButton.setEnabled(enable);
            if (enable) {

                enable = (buffetButton.isSelected()
                          || tableServiceButton.isSelected());
                foodList.setEnabled(endAtLeastOneHourAfterStart());
            } else {

                foodList.setEnabled(false);
                buffetButton.setSelected(false);
                tableServiceButton.setSelected(false);
            }
            okButton.setEnabled(foodList.isEnabled()
                                && foodList.getMinSelectionIndex()>-1);
        } // protectedEnforceInvariants()


        private boolean endAtLeastOneHourAfterStart() {
            Calendar startCalendar = getStartCalendar();
            if (startCalendar == null)
              return false;
            Calendar endCalendar = getEndCalendar();
            if (endCalendar == null)
              return false;
            startCalendar.add(Calendar.MINUTE, 59);
            return getEndCalendar().after(startCalendar);
        } // endAtLeastOneHourAfterStart()


        public void registerOkButton(JButton ok) {
            okButton = ok;
        } // registerOkButton(JButton)


        public void registerPeopleCountField(final JTextComponent field) {
            peopleCountField = field;
            DocumentAdapter documentAdapter = new DocumentAdapter() {
                protected void parseDocument() {
                    int count = PEOPLE_COUNT_DEFAULT;
                    try {
                        count = Integer.parseInt(field.getText());
                    } catch (NumberFormatException e) {
                    }
                    if (MIN_PEOPLE<=count && count<=MAX_PEOPLE )
                      peopleCount =  count;
                    else
                      peopleCount = PEOPLE_COUNT_DEFAULT;
                } // parseDocument()
              };
            field.getDocument().addDocumentListener(documentAdapter);
        } // registerPeopleCountField(JTextComponent)


        public void registerDateField(final JTextComponent field) {
            dateField = field;
            DocumentAdapter documentAdapter = new DocumentAdapter() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                protected void parseDocument() {
                    try {
                        date = dateFormat.parse(field.getText());
                    } catch (java.text.ParseException e) {
                        date = null;
                    } // try
                } // parseDocument()
              };
            field.getDocument().addDocumentListener(documentAdapter);
        } // registerDateField(JTextComponent)


        public void registerStartField(final JTextComponent field) {
            startField = field;
            DocumentAdapter documentAdapter = new DocumentAdapter() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                protected void parseDocument() {
                    try {
                        startTime = dateFormat.parse(field.getText());
                    } catch (java.text.ParseException e) {
                        startTime = null;
                    } // try
                } // parseDocument()
              };
            field.getDocument().addDocumentListener(documentAdapter);
        } // registerStartField(JTextComponent)


        public void registerEndField(final JTextComponent field) {
            endField = field;
            DocumentAdapter documentAdapter = new DocumentAdapter() {
                SimpleDateFormat dateFormat
                  = new SimpleDateFormat("HH:mm");
                protected void parseDocument() {
                    try {
                        endTime = dateFormat.parse(field.getText());
                    } catch (java.text.ParseException e) {
                        endTime = null;
                    } // try
                } // parseDocument()
              };
            field.getDocument().addDocumentListener(documentAdapter);
        } // registerEndField(JTextComponent)


        public void registerTableButton(JToggleButton button) {
            tableServiceButton = button;
            button.addItemListener(itemAdapter);
        } // registerEndField(JTextComponent)


        public void registerBuffetButton(JToggleButton button) {
            buffetButton = button;
            button.addItemListener(itemAdapter);
        } // registerEndField(JTextComponent)


        public void registerFoodList(JList list) {
            foodList = list;
            list.addListSelectionListener(listSelectionAdapter);
        } // registerEndField(JTextComponent)
    } // class BanquetMediator
    
    public class OkClick implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		JOptionPane.showMessageDialog(null, "Ваш заказ принят");
    	}
    }
    
    public class cancelButtonClick implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		countField.setText("");
    		dateField.setText("");
    		startField.setText("");
    		endField.setText("");
    	}
    }
    
	public static void main(String[] args) {
		new BanquetReservationDialog();
	}
} // class BanquetReservationDialog