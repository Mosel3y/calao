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

import java.util.Vector;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Exercise 
{
	Preferences appPrefs;
	
	int type; // 0 - notes in line, 1 - rhythm, 2 - score
	String title; // user defined exercise title
	int clefMask;
	Accidentals acc;
	int timeSign;
	int speed;
	int randomize;
	Vector<Note> notes;
	Vector<Note> notes2;
	
	public Exercise(Preferences p)
	{
		appPrefs = p;
		notes = new Vector<Note>();
		notes2 = new Vector<Note>();
		reset();
	}
	
	public void reset()
	{
		type = -1;
		title = "";
		clefMask = -1;
		acc = new Accidentals("", 0, appPrefs);
		timeSign = -1;
		speed = 60;
		randomize = 0;
		notes.clear();
		notes2.clear();
	}
	
	public void setType(int t)
	{
		type = t;
	}
	
	public void setTitle(String t)
	{
		System.out.println("Set exercise title to - " + t);
		title = t;
	}
	
	public void setClefMask(int mask)
	{
		System.out.println("Set exercise clefs mask to - " + mask);
		clefMask = mask;
	}
	
	public void setMeasure(int mes)
	{
		System.out.println("Set exercise measure to - " + mes);
		timeSign = mes;
	}
	
	public void setSpeed(int s)
	{
		System.out.println("Set exercise speed to - " + s);
		speed = s;
	}
	
	public void saveToXML()
	{
		try {
			 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("exercise");
			doc.appendChild(rootElement);

			// ************************* HEADER FIELDS ***********************************
			Element staff = doc.createElement("header");
			rootElement.appendChild(staff);

			// set attribute to staff element
			//Attr attr = doc.createAttribute("id");
			//attr.setValue("1");
			//staff.setAttributeNode(attr);

			// shorten way
			// staff.setAttribute("id", "1");
			Element exVersion = doc.createElement("version");
			exVersion.appendChild(doc.createTextNode(Integer.toString(2))); // remember to update this value in case more tags are added
			staff.appendChild(exVersion);

			Element exType = doc.createElement("type");
			exType.appendChild(doc.createTextNode(Integer.toString(type)));
			staff.appendChild(exType);

			Element exTitle = doc.createElement("title");
			exTitle.appendChild(doc.createTextNode(title));
			staff.appendChild(exTitle);

			Element clef = doc.createElement("clef");
			clef.appendChild(doc.createTextNode(Integer.toString(clefMask)));
			staff.appendChild(clef);
	 
			Element accType = doc.createElement("accType");
			accType.appendChild(doc.createTextNode(acc.getType()));
			staff.appendChild(accType);
			
			Element accCount = doc.createElement("accCount");
			accCount.appendChild(doc.createTextNode(Integer.toString(acc.getNumber())));
			staff.appendChild(accCount);
	 
			Element measure = doc.createElement("measure");
			measure.appendChild(doc.createTextNode(Integer.toString(timeSign)));
			staff.appendChild(measure);
			
			Element exSpeed = doc.createElement("speed");
			exSpeed.appendChild(doc.createTextNode(Integer.toString(speed)));
			staff.appendChild(exSpeed);
			
			Element exRandom = doc.createElement("random");
			exRandom.appendChild(doc.createTextNode(Integer.toString(randomize)));
			staff.appendChild(exRandom);
			
			
			// ************************ SEQUENCE ****************************
			Element exSequence = doc.createElement("sequence");
			rootElement.appendChild(exSequence);
			
			for (int i = 0; i < notes.size(); i++)
			{
				Element exNote = doc.createElement("note");
				exSequence.appendChild(exNote);
				
				Note tmpNote = notes.get(i);
				Element nType = doc.createElement("t");
				nType.appendChild(doc.createTextNode(Integer.toString(tmpNote.type)));
				exNote.appendChild(nType);		

				Element nPitch = doc.createElement("p");
				nPitch.appendChild(doc.createTextNode(Integer.toString(tmpNote.pitch)));
				exNote.appendChild(nPitch);

				Element nLevel = doc.createElement("l");
				nLevel.appendChild(doc.createTextNode(Integer.toString(tmpNote.level)));
				exNote.appendChild(nLevel);

				Element nTime = doc.createElement("ts");
				nTime.appendChild(doc.createTextNode(Double.toString(tmpNote.timestamp)));
				exNote.appendChild(nTime);				

				Element nDur = doc.createElement("d");
				nDur.appendChild(doc.createTextNode(Double.toString(tmpNote.duration)));
				exNote.appendChild(nDur);

				Element nClef = doc.createElement("c");
				nClef.appendChild(doc.createTextNode(Integer.toString(tmpNote.clef)));
				exNote.appendChild(nClef);	
				
				if (tmpNote.altType != 0)
				{
					Element nAlt = doc.createElement("a");
					nAlt.appendChild(doc.createTextNode(Integer.toString(tmpNote.altType)));
					exNote.appendChild(nAlt);	
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("Exercises" + File.separator + title + ".xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			
			transformer.transform(source, result);
	 
			System.out.println("XML File successfully saved !");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
	}
	
	
	private static String getTagValue(String sTag, Element eElement) 
	{
		NodeList elList = eElement.getElementsByTagName(sTag);
		if (elList == null || elList.getLength() == 0) return "-99";
		NodeList nlList = elList.item(0).getChildNodes();
		if (nlList == null || nlList.getLength() == 0) return "";
 
        Node nValue = (Node) nlList.item(0);
 
        if (nValue != null)
        	return nValue.getNodeValue();
        else
        	return "";
	}
	  
	public void loadFromFile(String path)
	{
		int levOffset = 0; // offset to be added to notes levels
		notes.clear();
		try
		{
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	 
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("header");
			//System.out.println("-----------------------");
	 
		    Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element eElement = (Element) nNode;
	 
				int version = Integer.parseInt(getTagValue("version", eElement));
				if (version < 0)
					levOffset = 2;
				type = Integer.parseInt(getTagValue("type", eElement));
				title = getTagValue("title", eElement);
				clefMask = Integer.parseInt(getTagValue("clef", eElement));
				acc = new Accidentals(getTagValue("accType", eElement), Integer.parseInt(getTagValue("accCount", eElement)), appPrefs);
				timeSign = Integer.parseInt(getTagValue("measure", eElement));
				speed = Integer.parseInt(getTagValue("speed", eElement));
				randomize = Integer.parseInt(getTagValue("random", eElement));

				System.out.println("Type: " + type);
			    System.out.println("Title: " + title);
			    System.out.println("Clef: " + clefMask);
		        System.out.println("accType: " + acc.getType());
			    System.out.println("accCount: " + acc.getNumber());
			    System.out.println("Measure: " + timeSign);
			    System.out.println("Speed: " + speed);
			    System.out.println("Randomize: " + randomize);
			}
			
			NodeList sList = doc.getElementsByTagName("sequence");
			// cycle through sequences
			for (int seq = 0; seq < nList.getLength(); seq++)
			{
			   Element sElem = (Element)sList.item(seq);
			   NodeList notesList = sElem.getElementsByTagName("note");
			   if (notesList != null)
			   {
				   System.out.println("Sequence #" + seq + ": notes: " + notesList.getLength());
				   for (int n = 0; n < notesList.getLength(); n++)
				   {
					   Note tmpNote;
					   Element nElem = (Element)notesList.item(n);
					   int nType = Integer.parseInt(getTagValue("t", nElem));
					   int nPitch = Integer.parseInt(getTagValue("p", nElem));
					   int nLevel = Integer.parseInt(getTagValue("l", nElem)) + levOffset;
					   double nStamp = Double.parseDouble(getTagValue("ts", nElem));
					   double nDur = Double.parseDouble(getTagValue("d", nElem));
					   int nClef = Integer.parseInt(getTagValue("c", nElem));
					   int nAlt = Integer.parseInt(getTagValue("a", nElem));
					   tmpNote = new Note(0, nClef, nLevel, nPitch, nType, false, 0);
					   if (nAlt > -3)
						   tmpNote.altType = nAlt;
					   tmpNote.setTimeStamp(nStamp);
					   tmpNote.duration = nDur;
					   notes.add(tmpNote);
				   }
			   }
			   
			}
			
		  } 
		  catch (Exception e) 
		  {
			e.printStackTrace();
		  }
	}
}
