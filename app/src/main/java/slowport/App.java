/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package slowport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.*;

import slowport.db.*;
import slowport.common.*;
import slowport.rater.*;
import slowport.slowapi.*;
import slowport.filter.*;
import slowport.views.*;

/**
 *
 * @author nafees
 */
public class App extends javax.swing.JFrame {

	/**
	 * Creates new form App
	 */
	public App() {
		initComponents();
		Connection conn = Connector.connect();
		try{
			timetableDB = new TimetableDB(conn);
			selectionDB = new SelectionDB(conn, timetableDB);
			noteDB = new NoteDB(conn);
		} catch (DBException e){
			e.printStackTrace();
			System.out.println("DAMNIT! freaking database!");
			System.exit(69);
		}
		sectionCheckboxes = new ArrayList<>();
		sectionCheckboxes.add(aCheckBox);
		sectionCheckboxes.add(bCheckBox);
		sectionCheckboxes.add(cCheckBox);
		sectionCheckboxes.add(dCheckBox);
		sectionCheckboxes.add(eCheckBox);
		sectionCheckboxes.add(fCheckBox);
		sectionCheckboxes.add(gCheckBox);
		sectionCheckboxes.add(hCheckBox);
		sectionCheckboxes.add(iCheckBox);
		sectionCheckboxes.add(jCheckBox);
		sectionCheckboxes.add(kCheckBox);
		sectionCheckboxes.add(lCheckBox);
		sectionCheckboxes.add(mCheckBox);
		sectionCheckboxes.add(nCheckBox);
		sectionCheckboxes.add(oCheckBox);
		sectionCheckboxes.add(pCheckBox);
		sectionCheckboxes.add(qCheckBox);
		sectionCheckboxes.add(rCheckBox);
		sectionCheckboxes.add(sCheckBox);
		sectionCheckboxes.add(tCheckBox);
		sectionCheckboxes.add(uCheckBox);
		sectionCheckboxes.add(vCheckBox);
		sectionCheckboxes.add(wCheckBox);
		sectionCheckboxes.add(xCheckBox);
		sectionCheckboxes.add(yCheckBox);
		sectionCheckboxes.add(zCheckBox);

		versions = timetableDB.getVersions();
		if (versions.size() > 0){
			selectedVersion = versions.get(versions.size() - 1);
			timetable = new Timetable(
					Session.deserializeAll(timetableDB.getTimetable(selectedVersion)));
			globTimetable = new Timetable(timetable);
		}else{
			versions = new ArrayList<>();
			selectedVersion = null;
			timetable = null;
			globTimetable = null;
		}

		// run dat updater
		updaterRun();
		new Timer(5 * 60 * 1000, new ActionListener() {
			public void actionPerformed(ActionEvent e){
				updaterRun();
			}
		});
	}

	private void updaterRun(){
		updateInProgress = true;
		updateProgress.setIndeterminate(true);
		Thread dhaga = new Thread(new Updater());
		dhaga.run();
		new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (updateInProgress)
					return; // its STILL going?
				((Timer)e.getSource()).stop(); // stop bothering me man, he's done
				// do the stuff that's done after timetable update
				loadTimetable();
				updateProgress.setIndeterminate(false);
			}
		}).start();
	}

	private void loadTimetable(){
		// disabled pieces of shits
		for (JCheckBox box : sectionCheckboxes){
			box.setEnabled(false);
			box.setSelected(false);
			box.setVisible(false);
		}

		versions = timetableDB.getVersions();
		if (versions.size() > 0){
			selectedVersion = versions.get(versions.size() - 1);
			timetable = new Timetable(
					Session.deserializeAll(timetableDB.getTimetable(selectedVersion)));
			globTimetable = new Timetable(timetable);
		}else{
			versions = new ArrayList<>();
			selectedVersion = null;
			timetable = null;
			globTimetable = null;
		}

		// load today's classes
		if (selectedVersion == null)
			return;
		{
			List<Session> sessions = selectionDB.getSelected(selectedVersion);
			// yeet those rows
			DefaultTableModel model =
				((DefaultTableModel)myTimetableTable.getModel());
			model.setRowCount(0);
			if (sessions != null){
				timetable = new Timetable(sessions);
				// filter by day
				List<Session> todays = new ArrayList<>();
				for (Session session : sessions){
					if (session.getDay().equals(LocalDate.now().getDayOfWeek()))
						todays.add(session);
				}
				List<String> todo = new ArrayList<>();
				YearWeek week = YearWeek.fromToday();
				for (Session session : sessions){
					List<Note> notes = noteDB.getNote(session.getName(),
							session.getSection());
					boolean added = false;
					for (Note note : notes){
						if (note.getWeek().equals(week) &&
								note.getSessionIndex() == session.getIndex()){
							todo.add(note.getNote());
							added = true;
							break;
						}
						// yeet older stuff
						if (note.getWeek().isBefore(week))
							noteDB.removeNote(note); // later looser
					}
					if (!added)
						todo.add("");
				}
				// shove those new rows in
				for (int i = 0; i < todays.size(); i ++){
					Session session = todays.get(i);
					String note = todo.get(i);
					model.addRow(new Object[]{
						session.getTime().toString(),
							session.getName() + " " + session.getSection(),
							session.getVenue(),
							note
					});
				}
			}
		}

		// now do myTimetableEditor
		{
			List<Session> sessions = selectionDB.getSelected(selectedVersion);
			if (sessions == null){
				myTimetableEditor.setText("");
			}else{
				myTimetableEditor.setContentType("text/html");
				myTimetableEditor.setText(
						TableMaker.generate((ArrayList<Session>)sessions));
			}
		}

		// now do My Courses
		{
			List<Session> sessions = selectionDB.getSelected(selectedVersion);
			if (sessions != null){
				Map<Session, List<Note>> notes = new HashMap<>();
				for (Session session : sessions){
					notes.put(session,
							noteDB.getNote(session.getName(), session.getSection()));
				}
				// TODO handle Todo list
				DefaultTableModel coursesSection =
					(DefaultTableModel)myCoursesTable.getModel();
				coursesSection.setRowCount(0); // YEET
				for (String course : timetable.getCourses()){
					for (String section : timetable.getSections(course))
						coursesSection.addRow(new Object[]{course, section});
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    tabMain = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    myTimetableTable = new javax.swing.JTable();
    jLabel2 = new javax.swing.JLabel();
    jPanel4 = new javax.swing.JPanel();
    jLabel6 = new javax.swing.JLabel();
    jScrollPane12 = new javax.swing.JScrollPane();
    myTimetableEditor = new javax.swing.JEditorPane();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    myCoursesTable = new javax.swing.JTable();
    jLabel3 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    addCourseCombo = new javax.swing.JComboBox<>();
    jLabel4 = new javax.swing.JLabel();
    addCourseSectionCombo = new javax.swing.JComboBox<>();
    jButton3 = new javax.swing.JButton();
    jButton4 = new javax.swing.JButton();
    jScrollPane6 = new javax.swing.JScrollPane();
    todoTable = new javax.swing.JTable();
    jScrollPane7 = new javax.swing.JScrollPane();
    todoText = new javax.swing.JTextArea();
    jLabel13 = new javax.swing.JLabel();
    weeksSpinner = new javax.swing.JSpinner();
    jLabel14 = new javax.swing.JLabel();
    sessionIndexSpinner = new javax.swing.JSpinner();
    jButton2 = new javax.swing.JButton();
    jButton6 = new javax.swing.JButton();
    jLabel15 = new javax.swing.JLabel();
    jPanel3 = new javax.swing.JPanel();
    jLabel9 = new javax.swing.JLabel();
    jScrollPane8 = new javax.swing.JScrollPane();
    combinatorCourseTable = new javax.swing.JTable();
    jButton5 = new javax.swing.JButton();
    jLabel16 = new javax.swing.JLabel();
    aCheckBox = new javax.swing.JCheckBox();
    eCheckBox = new javax.swing.JCheckBox();
    mCheckBox = new javax.swing.JCheckBox();
    iCheckBox = new javax.swing.JCheckBox();
    uCheckBox = new javax.swing.JCheckBox();
    qCheckBox = new javax.swing.JCheckBox();
    bCheckBox = new javax.swing.JCheckBox();
    fCheckBox = new javax.swing.JCheckBox();
    jCheckBox = new javax.swing.JCheckBox();
    nCheckBox = new javax.swing.JCheckBox();
    rCheckBox = new javax.swing.JCheckBox();
    vCheckBox = new javax.swing.JCheckBox();
    cCheckBox = new javax.swing.JCheckBox();
    gCheckBox = new javax.swing.JCheckBox();
    kCheckBox = new javax.swing.JCheckBox();
    oCheckBox = new javax.swing.JCheckBox();
    sCheckBox = new javax.swing.JCheckBox();
    wCheckBox = new javax.swing.JCheckBox();
    dCheckBox = new javax.swing.JCheckBox();
    hCheckBox = new javax.swing.JCheckBox();
    lCheckBox = new javax.swing.JCheckBox();
    pCheckBox = new javax.swing.JCheckBox();
    tCheckBox = new javax.swing.JCheckBox();
    xCheckBox = new javax.swing.JCheckBox();
    yCheckBox = new javax.swing.JCheckBox();
    zCheckBox = new javax.swing.JCheckBox();
    combinatorAddCourseCombo = new javax.swing.JComboBox<>();
    jButton7 = new javax.swing.JButton();
    jLabel17 = new javax.swing.JLabel();
    weightConsistencySpinner = new javax.swing.JSpinner();
    jLabel18 = new javax.swing.JLabel();
    weightGapSpinner = new javax.swing.JSpinner();
    jLabel19 = new javax.swing.JLabel();
    weightDaySpinner = new javax.swing.JSpinner();
    jButton8 = new javax.swing.JButton();
    jLabel20 = new javax.swing.JLabel();
    jLabel21 = new javax.swing.JLabel();
    jButton9 = new javax.swing.JButton();
    jButton10 = new javax.swing.JButton();
    jButton11 = new javax.swing.JButton();
    jScrollPane9 = new javax.swing.JScrollPane();
    combinatorTimetableTable = new javax.swing.JTable();
    jButton12 = new javax.swing.JButton();
    allCheckBox = new javax.swing.JCheckBox();
    jPanel6 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    jButton1 = new javax.swing.JButton();
    updateProgress = new javax.swing.JProgressBar();
    deltaACombo = new javax.swing.JComboBox<>();
    deltaBCombo = new javax.swing.JComboBox<>();
    jScrollPane5 = new javax.swing.JScrollPane();
    deltaTable = new javax.swing.JTable();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    jScrollPane4 = new javax.swing.JScrollPane();
    versionsTable = new javax.swing.JTable();
    jPanel5 = new javax.swing.JPanel();
    jLabel22 = new javax.swing.JLabel();
    jScrollPane10 = new javax.swing.JScrollPane();
    jTextArea2 = new javax.swing.JTextArea();
    jScrollPane11 = new javax.swing.JScrollPane();
    jTextArea3 = new javax.swing.JTextArea();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("SlowPort");
    setMinimumSize(new java.awt.Dimension(1000, 700));
    setResizable(false);

    tabMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    tabMain.setTabPlacement(javax.swing.JTabbedPane.LEFT);
    tabMain.setToolTipText("");
    tabMain.setName("tabMain"); // NOI18N

    jLabel1.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel1.setText("Welcome to SlowPort");

    myTimetableTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Time", "Class", "Venue", "ToDo"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    myTimetableTable.setShowGrid(true);
    myTimetableTable.getTableHeader().setResizingAllowed(false);
    myTimetableTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane1.setViewportView(myTimetableTable);

    jLabel2.setText("Today's Classes");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel1)
              .addComponent(jLabel2))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabMain.addTab("Home", jPanel1);

    jLabel6.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel6.setText("My Timetable");

    myTimetableEditor.setEditable(false);
    jScrollPane12.setViewportView(myTimetableEditor);

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane12)
          .addGroup(jPanel4Layout.createSequentialGroup()
            .addComponent(jLabel6)
            .addGap(0, 726, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel6)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabMain.addTab("My Timetable", jPanel4);

    myCoursesTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Course", "Section"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    myCoursesTable.getTableHeader().setResizingAllowed(false);
    myCoursesTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane2.setViewportView(myCoursesTable);

    jLabel3.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel3.setText("My Courses");

    jLabel5.setText("Add Course");

    addCourseCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Course" }));

    jLabel4.setText("Select Section");

    addCourseSectionCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Section" }));

    jButton3.setText(" Drop Selected Course");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });

    jButton4.setText("Add");

    todoTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Date", "ToDo"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    todoTable.getTableHeader().setResizingAllowed(false);
    todoTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane6.setViewportView(todoTable);

    todoText.setColumns(20);
    todoText.setRows(5);
    todoText.setToolTipText("ToDo Text");
    jScrollPane7.setViewportView(todoText);

    jLabel13.setText("Weeks From Now:");

    weeksSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

    jLabel14.setText("n-th class of Week:");

    sessionIndexSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

    jButton2.setText("Add");

    jButton6.setText("Drop Selected Todo");

    jLabel15.setText("Add Todo:");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addCourseSectionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jLabel3))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel5)
            .addGap(25, 25, 25)
            .addComponent(addCourseCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
              .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jScrollPane6)
              .addComponent(jScrollPane7)
              .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 0, Short.MAX_VALUE))
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(weeksSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sessionIndexSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton6)
            .addGap(18, 18, 18)
            .addComponent(jLabel15)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane7)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton3)
          .addComponent(jLabel13)
          .addComponent(weeksSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel14)
          .addComponent(sessionIndexSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jButton2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(addCourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(addCourseSectionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jButton4))
        .addContainerGap())
    );

    tabMain.addTab("My Courses", jPanel2);

    jLabel9.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel9.setText("Combinator");

    combinatorCourseTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Course", "Section"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    combinatorCourseTable.getTableHeader().setResizingAllowed(false);
    combinatorCourseTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane8.setViewportView(combinatorCourseTable);

    jButton5.setText(" Drop Selection");
    jButton5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton5ActionPerformed(evt);
      }
    });

    jLabel16.setText("Selection Criteria:");

    aCheckBox.setText("sectionA");
    aCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        aCheckBoxActionPerformed(evt);
      }
    });

    eCheckBox.setText("sectionE");

    mCheckBox.setText("sectionM");

    iCheckBox.setText("sectionI");

    uCheckBox.setText("sectionU");

    qCheckBox.setText("sectionQ");

    bCheckBox.setText("sectionB");

    fCheckBox.setText("sectionF");

    jCheckBox.setText("sectionJ");

    nCheckBox.setText("sectionN");

    rCheckBox.setText("sectionR");
    rCheckBox.setToolTipText("");

    vCheckBox.setText("sectionV");

    cCheckBox.setText("sectionC");

    gCheckBox.setText("sectionG");

    kCheckBox.setText("sectionK");

    oCheckBox.setText("sectionO");

    sCheckBox.setText("sectionS");

    wCheckBox.setText("sectionW");

    dCheckBox.setText("sectionD");

    hCheckBox.setText("sectionH");

    lCheckBox.setText("sectionL");

    pCheckBox.setText("sectionP");

    tCheckBox.setText("sectionT");

    xCheckBox.setText("sectionX");

    yCheckBox.setText("sectionY");

    zCheckBox.setText("sectionZ");

    combinatorAddCourseCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Course" }));

    jButton7.setText("Add Course");

    jLabel17.setText("Weightage for Consistency:");

    weightConsistencySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

    jLabel18.setText("Weightage for Gaps:");

    weightGapSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

    jLabel19.setText("Weightage for Days:");

    weightDaySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

    jButton8.setText("Generate");

    jLabel20.setText("Generated Timetable (lower score is better):");

    jLabel21.setText("Score:");

    jButton9.setText("Previous Best");

    jButton10.setText("Next Best");

    jButton11.setText("Graphical View");

    combinatorTimetableTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Day", "Start Time", "End Time", "Course", "Section"
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
    combinatorTimetableTable.setShowGrid(true);
    combinatorTimetableTable.getTableHeader().setResizingAllowed(false);
    combinatorTimetableTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane9.setViewportView(combinatorTimetableTable);

    jButton12.setText("Select");

    allCheckBox.setText("Select All");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 348, Short.MAX_VALUE))
              .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
              .addComponent(jScrollPane8))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(combinatorAddCourseCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7))
              .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(eCheckBox)
                      .addComponent(aCheckBox)
                      .addComponent(iCheckBox)
                      .addComponent(mCheckBox)
                      .addComponent(qCheckBox)
                      .addComponent(uCheckBox))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(fCheckBox)
                      .addComponent(bCheckBox)
                      .addComponent(jCheckBox)
                      .addComponent(nCheckBox)
                      .addComponent(rCheckBox)
                      .addComponent(vCheckBox))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(gCheckBox)
                      .addComponent(cCheckBox)
                      .addComponent(kCheckBox)
                      .addComponent(oCheckBox)
                      .addComponent(sCheckBox)
                      .addComponent(wCheckBox)))
                  .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(yCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(zCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(allCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                  .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(hCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(dCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel16)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(weightConsistencySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(weightGapSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(weightDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(jLabel20))
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel21)
              .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane9)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel9)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel16)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(combinatorAddCourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jButton7))
            .addGap(14, 14, 14)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(aCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(eCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(iCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(mCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(qCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(uCheckBox))
                  .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(bCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(fCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(nCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rCheckBox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(vCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(yCheckBox)
                  .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zCheckBox)
                    .addComponent(allCheckBox))))
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(cCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wCheckBox))
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(dCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xCheckBox)))))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton5)
          .addComponent(jButton8))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel17)
          .addComponent(weightConsistencySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel18)
          .addComponent(weightGapSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel19)
          .addComponent(weightDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addComponent(jLabel20)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel21)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton9)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton10)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton11)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton12)
            .addGap(0, 132, Short.MAX_VALUE))
          .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        .addContainerGap())
    );

    tabMain.addTab("Combinator", jPanel3);

    jLabel7.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel7.setText("Updates");

    jButton1.setText("Check for Updates");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    deltaACombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Version" }));

    deltaBCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Version" }));

    deltaTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Course", "Section", "New Venue", "New Time"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    deltaTable.getTableHeader().setResizingAllowed(false);
    deltaTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane5.setViewportView(deltaTable);

    jLabel10.setText("Version Delta:");

    jLabel11.setText("Version A:");

    jLabel12.setText("Version B:");

    versionsTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Version"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    versionsTable.getTableHeader().setResizingAllowed(false);
    versionsTable.getTableHeader().setReorderingAllowed(false);
    jScrollPane4.setViewportView(versionsTable);

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel7)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(updateProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(18, 18, 18)
            .addComponent(jButton1))
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel12)
                  .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(deltaACombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(deltaBCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
              .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 0, Short.MAX_VALUE)))))
        .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
      jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel6Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(updateProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel6Layout.createSequentialGroup()
            .addComponent(jLabel10)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(deltaACombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel11))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(deltaBCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel12))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane5))
          .addComponent(jScrollPane4))
        .addContainerGap())
    );

    tabMain.addTab("Updates", jPanel6);

    jLabel22.setFont(new java.awt.Font("sansserif", 0, 24)); // NOI18N
    jLabel22.setText("About SlowPort");

    jTextArea2.setEditable(false);
    jTextArea2.setColumns(20);
    jTextArea2.setRows(5);
    jTextArea2.setText("MIT License\n\nCopyright (c) 2023, Nafees Hassan, Usman Amjad, Kashif Raza, Abdul Mannan\n\nPermission is hereby granted, free of charge, to any person obtaining a copy\nof this software and associated documentation files (the \"Software\"), to deal\nin the Software without restriction, including without limitation the rights\nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\ncopies of the Software, and to permit persons to whom the Software is\nfurnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all\ncopies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\nSOFTWARE.");
    jScrollPane10.setViewportView(jTextArea2);

    jTextArea3.setEditable(false);
    jTextArea3.setColumns(20);
    jTextArea3.setRows(5);
    jTextArea3.setText("SlowPort is a Frontend for timetable management on Student's Side.\nIt aims to assist Students in finding the most suitable schedule as per their criteria.\n\nhttps://github.com/Nafees10/slowport");
    jTextArea3.setToolTipText("");
    jScrollPane11.setViewportView(jTextArea3);

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel22)
          .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
          .addComponent(jScrollPane11))
        .addContainerGap(303, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
      jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel5Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel22)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(116, Short.MAX_VALUE))
    );

    tabMain.addTab("About", jPanel5);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabMain)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabMain)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton3ActionPerformed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton1ActionPerformed

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton5ActionPerformed

  private void aCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aCheckBoxActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_aCheckBoxActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		// load those damn database
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			try {
				for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) { // NO WAY! MISTER NIMBUS!
						javax.swing.UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (ClassNotFoundException exx) {
				java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			} catch (IllegalAccessException exx) {
				java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			} catch (InstantiationException exx) {
				java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			} catch (UnsupportedLookAndFeelException exx) {
				java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
			}
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				App instance = new App();
				instance.setVisible(true);
				instance.updateProgress.setIndeterminate(true);
			}
		});
		System.out.println("Hallo");
	}

	private Connection conn;
	private SelectionDB selectionDB;
	private NoteDB noteDB;
	private TimetableDB timetableDB;

	private Timetable globTimetable;
	private Timetable timetable;
	private List<String> versions;
	private String selectedVersion;

	private List<JCheckBox> sectionCheckboxes;

	private boolean updateInProgress = false;
	private Map<String, String> updatedTimetables;
	private List<String> updatedVersions;
	private boolean updated = false;

	private boolean combinatorInProgress = false;
	private List<Timetable> combinatorResult;

	private class Updater implements Runnable{
		public void run(){
			List<String> remoteVersions = SlowApi.getVersions();
			if (remoteVersions == null){
				updateInProgress = false;
				return;
			}
			for (String remoteVersion : remoteVersions){
				if (versions.contains(remoteVersion))
					continue;
				// gotta fetch it
				versions.add(remoteVersion);
				String tt = SlowApi.getTimetable(remoteVersion);
				if (tt == null){
					updateInProgress = false;
					return;
				}
				timetableDB.addTimetable(remoteVersion, tt);
				tt = SlowApi.getMakeupTimetable(remoteVersion);
				if (tt != null)
					timetableDB.addTimetable(remoteVersion + "-makeup", tt);
				selectedVersion = remoteVersion;
				updated = true;
			}
			updateInProgress = false;
		}
	}

	private class Combinatorer implements Runnable{
		private Map<String, Set<String>> sections;
		public Combinatorer(Map<String, Set<String>> sections){
			this.sections = sections;
		}
		public void run(){
			Combinator daCombinator = new Combinator(timetable);
			combinatorResult = daCombinator.combinations(sections);
			combinatorInProgress = false;
		}
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox aCheckBox;
  private javax.swing.JComboBox<String> addCourseCombo;
  private javax.swing.JComboBox<String> addCourseSectionCombo;
  private javax.swing.JCheckBox allCheckBox;
  private javax.swing.JCheckBox bCheckBox;
  private javax.swing.JCheckBox cCheckBox;
  private javax.swing.JComboBox<String> combinatorAddCourseCombo;
  private javax.swing.JTable combinatorCourseTable;
  private javax.swing.JTable combinatorTimetableTable;
  private javax.swing.JCheckBox dCheckBox;
  private javax.swing.JComboBox<String> deltaACombo;
  private javax.swing.JComboBox<String> deltaBCombo;
  private javax.swing.JTable deltaTable;
  private javax.swing.JCheckBox eCheckBox;
  private javax.swing.JCheckBox fCheckBox;
  private javax.swing.JCheckBox gCheckBox;
  private javax.swing.JCheckBox hCheckBox;
  private javax.swing.JCheckBox iCheckBox;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton10;
  private javax.swing.JButton jButton11;
  private javax.swing.JButton jButton12;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JButton jButton4;
  private javax.swing.JButton jButton5;
  private javax.swing.JButton jButton6;
  private javax.swing.JButton jButton7;
  private javax.swing.JButton jButton8;
  private javax.swing.JButton jButton9;
  private javax.swing.JCheckBox jCheckBox;
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
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel22;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane10;
  private javax.swing.JScrollPane jScrollPane11;
  private javax.swing.JScrollPane jScrollPane12;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane4;
  private javax.swing.JScrollPane jScrollPane5;
  private javax.swing.JScrollPane jScrollPane6;
  private javax.swing.JScrollPane jScrollPane7;
  private javax.swing.JScrollPane jScrollPane8;
  private javax.swing.JScrollPane jScrollPane9;
  private javax.swing.JTextArea jTextArea2;
  private javax.swing.JTextArea jTextArea3;
  private javax.swing.JCheckBox kCheckBox;
  private javax.swing.JCheckBox lCheckBox;
  private javax.swing.JCheckBox mCheckBox;
  private javax.swing.JTable myCoursesTable;
  private javax.swing.JEditorPane myTimetableEditor;
  private javax.swing.JTable myTimetableTable;
  private javax.swing.JCheckBox nCheckBox;
  private javax.swing.JCheckBox oCheckBox;
  private javax.swing.JCheckBox pCheckBox;
  private javax.swing.JCheckBox qCheckBox;
  private javax.swing.JCheckBox rCheckBox;
  private javax.swing.JCheckBox sCheckBox;
  private javax.swing.JSpinner sessionIndexSpinner;
  private javax.swing.JCheckBox tCheckBox;
  private javax.swing.JTabbedPane tabMain;
  private javax.swing.JTable todoTable;
  private javax.swing.JTextArea todoText;
  private javax.swing.JCheckBox uCheckBox;
  private javax.swing.JProgressBar updateProgress;
  private javax.swing.JCheckBox vCheckBox;
  private javax.swing.JTable versionsTable;
  private javax.swing.JCheckBox wCheckBox;
  private javax.swing.JSpinner weeksSpinner;
  private javax.swing.JSpinner weightConsistencySpinner;
  private javax.swing.JSpinner weightDaySpinner;
  private javax.swing.JSpinner weightGapSpinner;
  private javax.swing.JCheckBox xCheckBox;
  private javax.swing.JCheckBox yCheckBox;
  private javax.swing.JCheckBox zCheckBox;
  // End of variables declaration//GEN-END:variables
}
