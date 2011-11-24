/***********************************************
This file is part of the ScoreDate project (http://www.mindmatter.it/scoredate/).

ScoreDate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ScoreDate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ScoreDate.  If not, see <http://www.gnu.org/licenses/>.

**********************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class ExercisesPanel extends JPanel implements TreeSelectionListener, ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = -1142716145008143198L;
	Font appFont;
	Preferences appPrefs;
	private ResourceBundle appBundle;
	private MidiController appMidi;
	
	private JPanel leftPanel;
	public RoundPanel topBar;
	public RoundedButton homeBtn;
	public RoundedButton newExerciseBtn;
	private JScrollPane treeScrollPanel = null;
	private JTree exercisesList;
	
	private Exercise newExercise;
	private Exercise selectedExercise;
	private ExerciseWizard exerciseTypeDialog;
	private ExerciseScoreWizard exerciseScoreSetupDialog;
	private ExerciseScoreEditor exerciseScoreEditorDialog;
	
	File exercisesDir; // the ScoreDate directory path
	
	public ExercisesPanel(Font f, ResourceBundle b, Preferences p, MidiController mc, Dimension d)
	{
		appFont = f;
		appBundle = b;
		appPrefs = p;
		appMidi = mc;
		
		setBackground(Color.white);
		setSize(d);
		setLayout(null);
		
		leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setBackground(Color.decode("0xCCF5FF"));
		Border defBorder = UIManager.getBorder(leftPanel);
		leftPanel.setBorder(BorderFactory.createTitledBorder(defBorder, "", TitledBorder.LEADING, TitledBorder.TOP));
		leftPanel.setBounds(5, 10, 330, d.height - 80);
		
		topBar = new RoundPanel(Color.decode("0xA3C7FF"), Color.decode("0xA2DDFF"));
		topBar.setBorderColor(Color.decode("0xA4D6FF"));
		topBar.setBounds(10, 7, 310, 67);
		topBar.setLayout(null);
		
		homeBtn = new RoundedButton("", appBundle);
		//homeBtn.setPreferredSize(new Dimension(buttonSize, buttonSize));
		homeBtn.setBounds(15, 5, 57, 57);
		homeBtn.setBackground(Color.decode("0x8FC6E9"));
		homeBtn.setButtonImage(new ImageIcon(getClass().getResource("/resources/home.png")).getImage());
		//homeBtn.setImagSize(32, 32);
		
		newExerciseBtn = new RoundedButton("", appBundle);
		//homeBtn.setPreferredSize(new Dimension(buttonSize, buttonSize));
		newExerciseBtn.setBounds(244, 5, 57, 57);
		newExerciseBtn.setBackground(Color.decode("0x8FC6E9"));
		newExerciseBtn.setButtonImage(new ImageIcon(getClass().getResource("/resources/new_exercise.png")).getImage());
		newExerciseBtn.addActionListener(this);
		
		topBar.add(homeBtn);
		topBar.add(newExerciseBtn);
		
		leftPanel.add(topBar);
		
		add(leftPanel);
		
		updateTreeList();
	}
	
    private class ExNodeInfo 
    {
        public String exLabel;
        public String filePath;

        public ExNodeInfo(String lbl, String path) 
        {
        	exLabel = lbl;
        	filePath = path;
        }

        public String toString() {
            return exLabel;
        }
    }
	
	public void walk( String path, DefaultMutableTreeNode parentNode ) 
	{
        File root = new File( path );
        File[] list = root.listFiles();

        for ( File f : list ) 
        {
            if ( f.isDirectory() ) 
            {
            	DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(f.getName());
            	parentNode.add(dirNode);
                walk( f.getAbsolutePath(), dirNode );
                System.out.println( "Dir: " + f.getAbsolutePath() );
            }
            else 
            {
            	if (f.getAbsolutePath().endsWith(".xml"))
            	{
            		ExNodeInfo nInfo = new ExNodeInfo(f.getName(), f.getAbsolutePath());
            		DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(nInfo);
            		dirNode.setUserObject(nInfo);
            		parentNode.add(dirNode);
            		System.out.println( "File: " + f.getAbsolutePath() );
            	}
            }
        }
    }

	private void updateTreeList()
	{
		exercisesDir = new File("Exercises");
		File EXdir = new File(exercisesDir.getAbsolutePath());
		File[] list = EXdir.listFiles();
		
		if (treeScrollPanel != null)
			leftPanel.remove(treeScrollPanel);
		
		if (list.length == 0)
		{
			DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(appBundle.getString("_noStatistics"));
			exercisesList = new JTree(mainNode);
		}
		else
		{
			DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode("Exercises");
			walk(EXdir.getAbsolutePath(), mainNode);
			exercisesList = new JTree(mainNode);
			exercisesList.setRootVisible(false);
			exercisesList.setShowsRootHandles(true);
			exercisesList.addTreeSelectionListener(this);
		}
		
		exercisesList.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		exercisesList.setBackground(Color.decode("0xCCF5FF"));
		exercisesList.setBounds(0, 0, 280, leftPanel.getHeight() - 100);
		
		treeScrollPanel = new JScrollPane(exercisesList);
		//Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		//treeScrollPanel.setBorder(border);
		treeScrollPanel.setBounds(10, 80, 300, leftPanel.getHeight() - 90);
		leftPanel.add(treeScrollPanel);
	}
	
	private void showExerciseSetup(int type)
	{
		exerciseTypeDialog.dispose();
		newExercise.setType(type);
		exerciseScoreSetupDialog = new ExerciseScoreWizard(appBundle, appPrefs, appFont, newExercise);
		exerciseScoreSetupDialog.setVisible(true);
		exerciseScoreSetupDialog.addPropertyChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == newExerciseBtn)
		{
			newExercise = new Exercise(appPrefs);
			exerciseTypeDialog = new ExerciseWizard(appBundle, appPrefs, appFont);
			exerciseTypeDialog.setVisible(true);
			exerciseTypeDialog.inlineExBtn.addActionListener(this);
			exerciseTypeDialog.rhythmExBtn.addActionListener(this);
			exerciseTypeDialog.scoreExBtn.addActionListener(this);
		}
		if (exerciseTypeDialog != null)
		{
			if(ae.getSource() == exerciseTypeDialog.inlineExBtn)
			{
				showExerciseSetup(0);
			}
			else if(ae.getSource() == exerciseTypeDialog.rhythmExBtn)
			{
				showExerciseSetup(1);
			}
			else if(ae.getSource() == exerciseTypeDialog.scoreExBtn)
			{
				showExerciseSetup(2);
			}
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName() == "gotoScoreEditor")
		{
			exerciseScoreEditorDialog = new ExerciseScoreEditor(appBundle, appPrefs, appFont, appMidi, newExercise);
			exerciseScoreEditorDialog.setVisible(true);
			exerciseScoreEditorDialog.addPropertyChangeListener(this);
		}
		else if (evt.getPropertyName() == "exerciseSaved")
		{
			updateTreeList();
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) 
	{
		//Returns the last path element of the selection.
	    DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)exercisesList.getLastSelectedPathComponent();
	    
	    if (selNode == null || selNode.isLeaf() == false) 
	    	return;

	    ExNodeInfo nInfo = (ExNodeInfo)selNode.getUserObject();
        System.out.println("Tree node clicked. Absolute path: " + nInfo.filePath);
        selectedExercise = new Exercise(appPrefs);
        selectedExercise.loadFromFile(nInfo.filePath);
	}
	
	protected void paintComponent(Graphics g) 
	{
		g.setColor(this.getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
