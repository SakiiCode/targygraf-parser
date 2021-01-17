package ml.sakii.targygrafparser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class Main{

	public static final String name="Név beáll.",code="Kód beáll.",credits="Kredit beáll.",prereqs="Előfeltétel hozzáad.";
	
	public static ArrayList<Subject> Subjects = new ArrayList<>();
	public static Subject SelectedSubject=new Subject();
	public static int index=0;
	static String currentKey="";
    static JLabel currentLabel = new JLabel();
    static JList<String> list;
    static JTextField exportname;
	static JScrollPane tableScrollPane;
	static JTable table;
	static JToggleButton nameButton, codeButton, creditsButton, prereqsButton;
	static JFrame frame;
    
    static JTable loadTable(String filename) {
    	String[][] data = CsvReader.readCsv(filename);
    	if(data == null) {
    		return null;
    	}
        String[] columnNames = new String[data[0].length];
        for(int i=0;i<columnNames.length;i++) {
        	columnNames[i]="";
        }
        
        TableModel tableModel = new DefaultTableModel(data, columnNames);
        final JTable table = new JTable(tableModel);
        table.setSelectionBackground(Color.blue);
		table.setSelectionForeground(Color.white);
        table.changeSelection(0, 0, false, false);
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                	if(!currentKey.equals("")) {
	                	if(evt.getButton() ==MouseEvent.BUTTON1) {
	                		saveProperty(currentKey,getValue(row, col));
	                	}else if(evt.getButton() == MouseEvent.BUTTON3) {
	                		appendProperty(currentKey," "+ getValue(row, col));
	                	}
                	}
                	
                	
                	if(nameButton.isSelected()) nameButton.doClick();
					if(codeButton.isSelected()) codeButton.doClick();
					if(creditsButton.isSelected()) creditsButton.doClick();
					if(prereqsButton.isSelected()) prereqsButton.doClick();


                }
            }
        });
        
        table.getInputMap().put(KeyStroke.getKeyStroke("control D"), "remline");
        table.getActionMap().put("remline", new AbstractAction() {

			private static final long serialVersionUID = 2425306497958182192L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedRows(table);				
			}
        	
        });
        

        return table;
    }
    
    static void updateRowHeights()
    {
        for (int row = 0; row < table.getRowCount(); row++)
        {
            int rowHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++)
            {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, rowHeight);
        }
    }
    
    
	public static void main(String[] args) {
		
	        frame = new JFrame("PDF - Tárgygráf konverter");
	        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	        
	        frame.setLayout(new BorderLayout());
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       
	        JPanel importPanel = new JPanel();
	        importPanel.setLayout(new FlowLayout());
	        JLabel pagesLabel = new JLabel("Oldalak:");
	        importPanel.add(pagesLabel);
	        final JTextField pagesField = new JTextField(10);
	        importPanel.add(pagesField);
	        JLabel filenameLabel = new JLabel("Fájlnév:");
	        importPanel.add(filenameLabel);
	        final JTextField importField = new JTextField(20);
	        importPanel.add(importField);
	        JButton importButton = new JButton("Importálás");
	        importButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(tableScrollPane != null) {
						frame.getContentPane().remove(tableScrollPane);
					}
					if(importField.getText().endsWith(".pdf")) {
						
						if(pagesField.getText().isEmpty()) {
							JOptionPane.showMessageDialog(frame.getContentPane(), "Nem adtál meg oldalakat!");
							return;
						}
						
						String filename = importField.getText().substring(0, importField.getText().length()-4)+"_converted.csv";
						String[] args  =new String[] {importField.getText(),"-p",pagesField.getText(),"-l","-o",filename};
						
						
						MySecurityManager secManager = new MySecurityManager();
						SecurityManager original = System.getSecurityManager();
					    System.setSecurityManager(secManager);
					    
					    try {
					    	technology.tabula.CommandLineApp.main(args);
					    } catch (SecurityException e1) {
					    	if(!e1.getMessage().equals("0")) {
						    	e1.printStackTrace();
					        	JOptionPane.showMessageDialog(frame.getContentPane(), "PDF konvertálási hiba ("+e1.getMessage()+")");
					        	System.setSecurityManager(original);
					        	return;
					    	}

					    }
					    System.setSecurityManager(original);
					
					    if(loadCsv(filename)) {
					    	importField.setText(filename);
					    }
			        
					}else if(importField.getText().endsWith(".csv")) {
					    loadCsv(importField.getText());
					}else {
			        	JOptionPane.showMessageDialog(frame.getContentPane(), "Nem pdf fájl!");
			        }
					
					
				}
	        	
				private boolean loadCsv(String filename) {
					try {
				    	table = loadTable(filename);
				    }catch(Exception e2) {
				    	JOptionPane.showMessageDialog(frame.getContentPane(), "CSV Betöltési Hiba\r\n"+e2.getMessage());
				    	return false;
				    }
				        
			        updateRowHeights();
			        tableScrollPane = new JScrollPane(table);
			        tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			        frame.getContentPane().add(tableScrollPane,BorderLayout.CENTER);
			        frame.revalidate();
			        return true;
				}
	        		        	
	        });
	        importPanel.add(importButton);
	        
	        JButton saveCsvButton = new JButton("CSV mentése");
	        saveCsvButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(table==null) {
						JOptionPane.showMessageDialog(frame.getContentPane(), "Nincs betöltött tanterv!");
					}else {
						ArrayList<String[]> data = new ArrayList<>();
		
						  for(int i = 0; i < table.getRowCount(); i++){
							  String[] row = new String[table.getColumnCount()];
							  for(int j=0;j<table.getColumnCount();j++) {
								  row[j]=getValue(i, j).toString();
							  }
							  data.add(row);
		
						  }
						CsvReader.WriteCsv(importField.getText(), data);
					}					

					
				}
	        	
	        });
	        importPanel.add(saveCsvButton);
	        
	        frame.getContentPane().add(importPanel, BorderLayout.PAGE_START);
	        
	        
	        JPanel configPanel = new JPanel();
	        configPanel.setLayout(new BoxLayout(configPanel,BoxLayout.Y_AXIS));

	        nameButton = new JToggleButton(name);
	        codeButton = new JToggleButton(code);
	        creditsButton = new JToggleButton(credits);
	        prereqsButton = new JToggleButton(prereqs);

	        
	        ActionListener propertyListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals(name) && nameButton.isSelected()) {
						table.setSelectionBackground(Color.blue);
						table.setSelectionForeground(Color.white);
						if(codeButton.isSelected()) codeButton.doClick();
						if(creditsButton.isSelected()) creditsButton.doClick();
						if(prereqsButton.isSelected()) prereqsButton.doClick();
					}else if(e.getActionCommand().equals(code) && codeButton.isSelected()) {
						table.setSelectionBackground(Color.yellow);
						table.setSelectionForeground(Color.black);
						if(nameButton.isSelected()) nameButton.doClick();
						if(creditsButton.isSelected()) creditsButton.doClick();
						if(prereqsButton.isSelected()) prereqsButton.doClick();
					}else if(e.getActionCommand().equals(credits)&& creditsButton.isSelected()) {
						table.setSelectionBackground(Color.LIGHT_GRAY);
						table.setSelectionForeground(Color.black);
						if(nameButton.isSelected()) nameButton.doClick();
						if(codeButton.isSelected()) codeButton.doClick();
						if(prereqsButton.isSelected()) prereqsButton.doClick();
					}else if(e.getActionCommand().equals(credits)&& prereqsButton.isSelected()) {
						table.setSelectionBackground(Color.LIGHT_GRAY);
						table.setSelectionForeground(Color.black);
						if(nameButton.isSelected()) nameButton.doClick();
						if(codeButton.isSelected()) codeButton.doClick();
						if(creditsButton.isSelected()) creditsButton.doClick();
					}
					
					if(nameButton.isSelected() || codeButton.isSelected() || creditsButton.isSelected() || prereqsButton.isSelected()) {
						currentKey=e.getActionCommand();
					}else {
						currentKey="";
					}
				}
	        	
	        };
	        
	        nameButton.setMnemonic(KeyEvent.VK_1);
	        nameButton.setActionCommand(name);
	        nameButton.addActionListener(propertyListener);
	        configPanel.add(nameButton,Component.LEFT_ALIGNMENT);

	        
	        codeButton.setMnemonic(KeyEvent.VK_2);
	        codeButton.setActionCommand(code);
	        codeButton.addActionListener(propertyListener);
	        configPanel.add(codeButton,Component.LEFT_ALIGNMENT);

	        
	        creditsButton.setMnemonic(KeyEvent.VK_3);
	        creditsButton.setActionCommand(credits);
	        creditsButton.addActionListener(propertyListener);
	        configPanel.add(creditsButton,Component.LEFT_ALIGNMENT);
	        
	        prereqsButton.setMnemonic(KeyEvent.VK_4);
	        prereqsButton.setActionCommand(prereqs);
	        prereqsButton.addActionListener(propertyListener);
	        configPanel.add(prereqsButton,Component.LEFT_ALIGNMENT);

	        
	        ActionListener prereqListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String selectedCell = getValue(table.getSelectedRow(), table.getSelectedColumn());
					if(e.getActionCommand().equals("add")) {
						SelectedSubject.getPrereqs().add(selectedCell);
					}else if(e.getActionCommand().equals("remove")) {
						SelectedSubject.getPrereqs().clear();
					}
					
					redrawSelectedSubject();
					
				}
	        	
	        };
	        

	        
	        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
	        separator.setMaximumSize( new Dimension(Integer.MAX_VALUE, 1) );
	        configPanel.add(separator);
	        
	        JButton remPrereq = new JButton ("Minden előfelt. törlése");
	        remPrereq.setMnemonic(KeyEvent.VK_5);
	        remPrereq.setActionCommand("remove");
	        remPrereq.addActionListener(prereqListener);
	        configPanel.add(remPrereq);



	        
	        

	        

	        JButton saveSubject = new JButton("Mentés");
	        saveSubject.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Subjects.add(SelectedSubject);
					if(!((DefaultListModel<String>)( list.getModel())).contains(SelectedSubject.getName())) {
						((DefaultListModel<String>)( list.getModel())).addElement(SelectedSubject.getName());
					}
					setSelectedSubject(new Subject());
                	//nameButton.doClick();

				}
	        	
	        });
	        configPanel.add(saveSubject,Component.LEFT_ALIGNMENT);

	        
	        
	        
	        
	        
	        list = new JList<String>(new DefaultListModel<String>());
	        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        list.setLayoutOrientation(JList.VERTICAL);
	        list.setVisibleRowCount(-1);
	        
	        
	        
            list.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					int index = list.getSelectedIndex();
					if(index != -1) {
						setSelectedSubject(Subjects.get(index));
					}
					
				}
            	
            });
            
	        JScrollPane listScroller = new JScrollPane(list);
	        listScroller.setPreferredSize(new Dimension(250, 80));
	            
            configPanel.add(listScroller,Component.LEFT_ALIGNMENT);
	            
            redrawSelectedSubject();
	        configPanel.add(currentLabel,Component.LEFT_ALIGNMENT);
            
            
            frame.add(configPanel,BorderLayout.LINE_END);
            

            
            JPanel exportPanel = new JPanel();
            exportPanel.setLayout(new FlowLayout());
            
            final JSpinner semesterSpinner = new JSpinner();

            ActionListener autofillListener = new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					int startCol=1;
					int startRow=1;
					ArrayList<Subject> subjects = new ArrayList<>();
					int semester=1;
					String currentGroup="";
					boolean obligatory = e.getActionCommand().equals("obligatory");
					for(int i=startRow;i<table.getRowCount();i++) {
						String code = getValue(i, startCol);
						if(!getValue(i,startCol+2).equals("")) {
					
							if(getValue(i, 0).equals("Összesen")){
								i++;
								semester++;
								continue;
							}
							
							Subject s = new Subject();
							
							
							
							
							
							
							//előfeltételek
							String prereqsCell = getValue(i, startCol+4).replace("-", "");
							if(!prereqsCell.isEmpty()) {
								ArrayList<String> prereqs = new ArrayList<String>(new ArrayList<>(Arrays.asList(prereqsCell.split("\r\n"))));
								for(int j=0;j<prereqs.size();j++) {
									if(!prereqs.get(j).startsWith("VE")) {
										if(prereqs.get(j).startsWith("(")) { //ph. elofelt.
											prereqs.set(j, "(VE"+prereqs.get(j).substring(1));
										}else {
											prereqs.set(j, "VE"+prereqs.get(j));
										}
									}
								}
								s.setPrereqs(prereqs);
							}
							
							
							
							
							
							//kreditek
							String creditCell = getValue(i, startCol+2);
							
							try {
								s.setCredits(Integer.parseInt(creditCell.split("\r\n")[0]));
							}catch(Exception e1) {
								e1.printStackTrace();
								JOptionPane.showMessageDialog(frame.getContentPane(), "Kredit hiba ("+i+":"+(startCol+2)+")\r\n"+e1.getMessage());
							}

							
							//név
							String[] nameArr = getValue(i, startCol-1).split("\r\n");
							
							StringBuilder sb = new StringBuilder();
							for(int j=0;j<nameArr.length/2;j++) {
								sb.append(nameArr[j]+" ");
							}
							
							if(nameArr[0].equals("Mesterséges")) {
								System.out.println(Arrays.toString(nameArr));
							}
							boolean odd = (nameArr.length % 2) == 1;
							if(odd) {
								String middleLine = nameArr[nameArr.length/2];
								if(Character.isLowerCase(middleLine.charAt(0)) || middleLine.charAt(0) == '(') { //magyar
									sb.append(middleLine);
								}
							}
							

							
							s.setName(sb.toString().trim());
							
							//kód
							
							if(s.getName().equals("Szabadon választható tárgy")) {
								s.setCode("___OPTIONAL___");
							}else if(code.equals("")){  //targycsoport hozzarendeles
								s.setCode(null);
								s.course_block_references.add(s.getName());
							}else {
								if(code.startsWith("VE")) {
									s.setCode(code);
								}else {
									s.setCode("VE"+code);	
								}
							}
							
							
							
							if(obligatory) { //félév
								s.setSemester(semester);
							}else { // tárgycsoport
								s.setGroup(currentGroup);
							}
							
							
							subjects.add(s);
							
							
							
						}else if(!obligatory && getValue(i,1).equals("")) {
							currentGroup = getValue(i,0);
							
						}else {
							JOptionPane.showMessageDialog(frame.getContentPane(), "Üres cella ebben a sorban:"+i);
						}
						
					}
					
					if(obligatory) {
						semesterSpinner.setValue(semester-1);
					}
					
					for(Subject s : subjects) {
						Subjects.add(s);
						((DefaultListModel<String>)( list.getModel())).addElement(s.getName());

					}
				}
			};
			
			JButton autofillObl = new JButton("Auto-parse (Kötelező tárgyak)");
			autofillObl.setActionCommand("obligatory");
			autofillObl.addActionListener(autofillListener);
            JButton autofillCho = new JButton("Auto-parse (Tárgycsoportok)");
            autofillCho.setActionCommand("choosable");
            autofillCho.addActionListener(autofillListener);
            
            //-t típusú parseolás
            /*autofill.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					int startCol = 1;
					int startRow = 0;
					int found=0;
					for(int i=0;i<table.getRowCount();i++) {
						if(!table.getValueAt(i, 0).toString().equals("")) {
							found++;
						}
						if(found==2) {
							startRow=i;
							break;
						}
					}
					
					int nameCol = startCol-1;
					int creditsCol = startCol+2;
					int prereqsCol = startCol+4;
					
					int padding=0;
					ArrayList<Subject> subjects = new ArrayList<>();
					for(int i=startRow;i<table.getRowCount();i++) {
						String val = table.getValueAt(i, startCol).toString();
						if(!val.equals("")) {
							Subject s = new Subject();
							s.setCode("VE"+val);
							if(padding==1) {
								s.setName(table.getValueAt(i-1, nameCol).toString());
							}else if(padding==2) {
								s.setName(table.getValueAt(i-2, nameCol).toString() + " " + table.getValueAt(i-1, nameCol).toString());
							}else if(padding ==3) {
								s.setName(table.getValueAt(i-3, nameCol).toString() + " " + table.getValueAt(i-3,nameCol).toString()+
										" "+table.getValueAt(i-3, nameCol).toString());
							}
							int credits=0;
							
							try {
								String credits1=table.getValueAt(i, creditsCol).toString();
								credits = Integer.parseInt(credits1);
							}catch(NumberFormatException e1) {
								try {
									String credits2=table.getValueAt(i-1, creditsCol).toString();
									credits = Integer.parseInt(credits2);									
								}catch(Exception e2) {
									System.err.println(e2.getMessage());
									JOptionPane.showMessageDialog(frame.getContentPane(), "Formátum hiba a krediteknél ("+(i-1)+":"+creditsCol+")", "Hiba", JOptionPane.ERROR_MESSAGE);
									return;
								}
							}
							
							s.setCredits(credits);
							
							
							
							ArrayList<String> prereqs = new ArrayList<>();
							String prereq1 = table.getValueAt(i, prereqsCol).toString();
							
							if(!prereq1.equals("-")) { // 1-3 elofeltetel
								
								if(prereq1.equals("")) { // 2 elofeltetel
									String prereq11 = table.getValueAt(i-1, prereqsCol).toString();
									String prereq12 = table.getValueAt(i+1, prereqsCol).toString();
									if(prereq11.equals("") || prereq12.equals("")) {
										JOptionPane.showMessageDialog(frame.getContentPane(), "Formátum hiba az előfeltételeknél ("+i+":"+prereqsCol+")", "Hiba", JOptionPane.ERROR_MESSAGE);
										return;
									}else {
										prereqs.add(prereq11);
										prereqs.add(prereq12);
									}
								}else {
									if(padding>1) { // 1 v 3 elofeltetel
										String prereq11 = table.getValueAt(i-2, prereqsCol).toString();
										if(prereq11.equals("")) {// 1 elofeltetel
											prereqs.add(prereq1);
										}else { // 3 elofeltetel
											prereqs.add(prereq11);
											String prereq12 = table.getValueAt(i-2, prereqsCol).toString();
											prereqs.add(prereq12);
											String prereq13 = table.getValueAt(i+2, prereqsCol).toString();
											prereqs.add(prereq13);
										}
									}else { // 1 elofeltetel
										prereqs.add(prereq1);
									}
								}
								
							}
							
							for(String prereq : prereqs) {
								s.getPrereqs().add("VE"+prereq);
							}
							
							subjects.add(s);
							padding=-padding;
						}else {
							padding++;
						}
					}
					
					for(Subject s : subjects) {
						Subjects.put(s.getCode(), s);
						if(!((DefaultListModel<String>)( list.getModel())).contains(s.getCode())) {
							((DefaultListModel<String>)( list.getModel())).addElement(s.getCode());
						}
						//setSelectedSubject(new Subject());
					}
					
				}
            	
            	
            	
            	
            });*/
            
            
            exportPanel.add(autofillObl);
            exportPanel.add(autofillCho);
            JLabel semesterLabel = new JLabel("Összes félévek:");
            exportPanel.add(semesterLabel);
            semesterSpinner.setValue(1);
            exportPanel.add(semesterSpinner);
            
            
            exportname = new JTextField(20);
	        exportPanel.add(exportname);
	        
	        JButton exportButton = new JButton("Exportálás (*.json)");
	        exportButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					
			        JSONObject rootObject = new JSONObject();
			        rootObject.put("name", "");
			        rootObject.put("description", "");
			        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			        rootObject.put("curriculum_updated_at", formatter.format(new Date()));
			        rootObject.put("name", "");
			        
			        JSONArray course_blocks=new JSONArray();
			        
			        
			        HashMap<Integer,ArrayList<Subject>> obligatorySubjects = new HashMap<>();
			        
			        HashMap<String,ArrayList<Subject>> choosableSubjects = new HashMap<>();
			        
			        
			        for(Subject s : Subjects) {
			        	String group = s.getGroup();
			        	if(group=="") {
			        		if(!obligatorySubjects.containsKey(s.getSemester())) {
			        			obligatorySubjects.put(s.getSemester(), new ArrayList<>());
			        		}
			        		obligatorySubjects.get(s.getSemester()).add(s);
			        	}else {
			        		if(!choosableSubjects.containsKey(group)) {
			        			choosableSubjects.put(group, new ArrayList<>());
			        		}
			        		choosableSubjects.get(group).add(s);
			        	}
			        }
			        
			        
			        
			        
			        
			        
			        // kotelezo targyak
			        
			        for(int i=1;i<=Integer.parseInt(semesterSpinner.getValue().toString());i++) {
			        	JSONObject block = new JSONObject();
			        	block.put("name", i+". félév");
			        	block.put("row", 0);
			        	
			        	JSONArray courses = new JSONArray();

						for(Subject s : obligatorySubjects.get(i)) {

							JSONObject subject = new JSONObject();
							subject.put("code", s.getCode()==null?JSONObject.NULL:s.getCode());
							subject.put("name", s.getName());
							subject.put("credits",s.getCredits());
							if(!s.getPrereqs().isEmpty()) {
								subject.put("prerequisites", s.getPrereqs());
							}
							if(!s.course_block_references.isEmpty()) {
								subject.put("course_block_references", s.course_block_references);
							}
							courses.put(subject);
						}
						
						
						block.put("courses", courses);
						
						course_blocks.put(block);
			        	
			        	
			        }
			        
			        // kotval targyak
			        
			        for(String groupName : choosableSubjects.keySet()) {
			        	JSONObject block = new JSONObject();

			        	block.put("name", groupName);
			        	block.put("row", 1);
			        	
			        	JSONArray courses = new JSONArray();

						for(Subject s : choosableSubjects.get(groupName)) {

							JSONObject subject = new JSONObject();
							subject.put("code", s.getCode());
							subject.put("name", s.getName());
							subject.put("credits",s.getCredits());
							if(!s.getPrereqs().isEmpty()) {
								subject.put("prerequisites", s.getPrereqs());
							}
							courses.put(subject);
						
						}
						
						block.put("courses", courses);
						
						course_blocks.put(block);
			        	
			        	
			        }
			        
			        
			        
			        
			        rootObject.put("course_blocks", course_blocks);
			        
			        					
					String json = rootObject.toString(4);
					
					
					
					try (FileOutputStream os = new FileOutputStream(exportname.getText()+".json")){
						os.write(json.getBytes());
						JOptionPane.showMessageDialog(frame.getContentPane(), "Sikeres mentés!");
					} catch (IOException e1) {
						 
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame.getContentPane(), "Sikertelen mentés:"+e1.getMessage());
					}
				}
	        	
	        });
	        exportPanel.add(exportButton);
	        
	        frame.add(exportPanel,BorderLayout.PAGE_END);
	        
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	        
	        
	        
	        
	        
	}
	
	public static void removeSelectedRows(JTable table){
		DefaultTableModel model = (DefaultTableModel) (table.getModel());
		int[] rows = table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			model.removeRow(rows[i]-i);
		}
		table.setModel(model);
		}
	
	private static String getValue(int row, int column) {
		String raw =  table.getValueAt(row, column).toString();
		
		return raw.substring(6, raw.length()-7).replace("<br>", "\r\n");
	}
	
	public static void saveProperty(String key, String value) {
		switch(key) {
		case name: SelectedSubject.setName(value);break;
		case code: SelectedSubject.setCode(value);break;
		case credits: SelectedSubject.setCredits(Integer.parseInt(value));break;
		case prereqs: SelectedSubject.getPrereqs().add(value);
		}
		redrawSelectedSubject();
	}
	
	public static void appendProperty(String key, String value) {
		switch(key) {
		case name: SelectedSubject.setName(SelectedSubject.getName() + value);break;
		case code: SelectedSubject.setCode(SelectedSubject.getCode() + value);break;
		case credits: SelectedSubject.setCredits(Integer.parseInt(value));break;
		case prereqs: SelectedSubject.getPrereqs().add(value);
		}
		
		redrawSelectedSubject();
	}
	
	

	private static void redrawSelectedSubject() {
		currentLabel.setText("<html><body>Aktuális tantárgy:"+SelectedSubject.getName()+"<br>"
				+ "Kód:"+SelectedSubject.getCode()+"<br>"
				+ "Kreditek:"+SelectedSubject.getCredits()+"<br>"
				+ "Előfeltételek:"+SelectedSubject.getPrereqs()+"</body></html>");
        currentLabel.setPreferredSize(new Dimension(300, 100));
		if(((DefaultListModel<String>)( list.getModel())).contains(SelectedSubject.getCode())) {
			list.setSelectedValue(SelectedSubject.getCode(), true);
		}else {
			list.clearSelection();
		}

		
	}
	
	private static void setSelectedSubject(Subject s) {
		SelectedSubject=s;
		redrawSelectedSubject();
	}




}
