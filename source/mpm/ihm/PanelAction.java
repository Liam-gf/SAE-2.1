/**
* Classe PanelAction : Permet de d'afficher et de gérer tout les boutons qui vont agir sur notre Mpm
* Groupe : 5
* @author : Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import mpm.Controleur;
import mpm.metier.Mpm;
import mpm.metier.Tache;


public class PanelAction extends JPanel implements ActionListener, TableModelListener
{
	private Controleur        ctrl;
	
	private JButton           btnTard, btnTot, btnCheminCritique;
	private JButton           btnAjt,  btnEnl, btnValid;
	
	private ButtonGroup       groupeBtnRadio;
	private JRadioButton      rbDteManuelle, rbDteEntier;
	
	private JTextField        textDateManuelle;
	
	private JTable            table;
	private DefaultTableModel tableModel;
	
	private JMenuBar          menuBar;
	private JMenu             menuFichier;
	private JMenuItem         itemChoisirFichier, itemEnregistrer;  
	
	private JPanel            panelTableau;
	
	private int               cptAfficheNiveau;
	
	private List<Tache>       listTacheDyn;
	
	/**
	* Constructeur de la classe PanelAction.
	* Initialise le panel.
	* Le tableau est initialisé avec les données des tâches récupérées depuis le contrôleur.
	*
	* @param ctrl le contrôleur principal de l'application, utilisé pour la gestion des données et des événements
	*/
	public PanelAction( Controleur ctrl )
	{
		GregorianCalendar cal;
		JPanel            panelBoutons;
		JPanel            panelBtnTableau;
		
		String[] colonnes = { "Nom", "Durée", "Précédent", "Suivant" };
		
		
		this.ctrl             = ctrl;
		this.cptAfficheNiveau = 0;
		
		this.setLayout( new BorderLayout() );
		
		/* ------------------------------ */
		/* Création des composants        */
		/* ------------------------------ */
		
		// --- Partie boutons inférieurs
		panelBoutons = new JPanel( new GridLayout( 2, 3 ) );
		panelBoutons.setOpaque(false);
		
		this.panelTableau = new JPanel( new BorderLayout() );
		
		panelBtnTableau   = new JPanel();
		panelBtnTableau.setBackground( new Color( 0xffe6e6 ) );
		
		this.btnTard           = new JButton( "+ Tard"          );
		this.btnTot            = new JButton( "+ Tot"           );
		this.btnCheminCritique = new JButton( "Chemin Critique" );
		
		this.btnTard.setBackground( new Color ( 0xEC8686 ) );
		this.btnTot .setBackground( new Color ( 0x9DE09D ) );
		
		this.btnTard.setForeground( Color.BLACK );
		this.btnTot .setForeground( Color.BLACK );
		
		this.btnAjt   = new JButton( "Ajouter" );
		this.btnEnl   = new JButton( "Enlever" );
		this.btnValid = new JButton( "Valider" );
		
		this.rbDteEntier   = new JRadioButton( "Date en chiffres"    );
		this.rbDteManuelle = new JRadioButton( "Saisie date début :" );
		this.rbDteEntier.setSelected(true);
		
		cal                   = new GregorianCalendar();
		this.textDateManuelle = new JTextField( String.format( "%02d", cal.get( Calendar.DAY_OF_MONTH )     ) + "/" +
		                                        String.format( "%02d", cal.get( Calendar.MONTH        ) + 1 ) + "/" +
		                                                               cal.get( Calendar.YEAR         ) % 100         );
		
		this.groupeBtnRadio = new ButtonGroup();
		groupeBtnRadio.add( this.rbDteManuelle );
		groupeBtnRadio.add( this.rbDteEntier   );
		
		
		this.tableModel = new DefaultTableModel(colonnes, 0)
		{
			public boolean isCellEditable( int lig, int col )
			{
				return true;
			}
		};
		
		this.table = new JTable( this.tableModel );
		
		this.menuBar              = new JMenuBar();
		this.menuFichier          = new JMenu    (   "Fichier"         );
		this.itemChoisirFichier   = new JMenuItem("Choisir fichier" );
		this.itemEnregistrer      = new JMenuItem("Enregistrer Sous"     );
		
		/* ------------------------------ */
		/* Positionnement des composants  */
		/* ------------------------------ */
		
		panelBtnTableau.add( this.btnAjt   );
		panelBtnTableau.add( this.btnEnl   );
		panelBtnTableau.add( this.btnValid );
		
		
		panelBoutons.add( this.rbDteEntier       );
		panelBoutons.add( this.rbDteManuelle     );
		panelBoutons.add( this.textDateManuelle  );
		
		panelBoutons.add( this.btnTot            );
		panelBoutons.add( this.btnTard           );
		panelBoutons.add( this.btnCheminCritique );
		
		
		this.panelTableau.add( new JScrollPane( this.table ), BorderLayout.CENTER );
		this.panelTableau.add( panelBtnTableau,               BorderLayout.SOUTH  );
		
		this.add( panelTableau, BorderLayout.CENTER );
		this.add( panelBoutons, BorderLayout.SOUTH  );
		
		
		this.listTacheDyn = this.ctrl.getMpm().getTaches();
		
		for ( Tache t : this.listTacheDyn )
		{
			// On met les noms des tâches précédentes séparés par des virgules dans la colonne "Voisin(s)"
			if ( ! t.getNom().equals( "Début" ) && ! t.getNom().equals( "Fin" ) )
			{
				tableModel.addRow( new Object[] { t.getNom(), t.getDuree(), getNomsTaches( t.getPrc() ), getNomsTaches( t.getSvt() ) } );
			}
		}
		
		this.menuFichier.add( this.itemChoisirFichier  );
		this.menuFichier.add( this.itemEnregistrer     );
		
		this.menuBar.add( menuFichier );
		
		/* ------------------------------ */
		/* Activation des composants      */
		/* ------------------------------ */
		
		this.btnTard             .setEnabled( false );
		this.btnCheminCritique   .setEnabled( false );
		
		this.btnAjt              .addActionListener( this );
		this.btnEnl              .addActionListener( this );
		this.btnValid            .addActionListener( this );
		
		this.rbDteEntier         .addActionListener( this );
		this.rbDteManuelle       .addActionListener( this );
		
		this.btnTard             .addActionListener( this );
		this.btnTot              .addActionListener( this );
		this.btnCheminCritique   .addActionListener( this );
		
		this.itemChoisirFichier  .addActionListener( this );
		this.itemEnregistrer     .addActionListener( this );
		
		this.tableModel          .addTableModelListener( this );
	}


	public void        setListTacheDyn( ArrayList<Tache> t ) { this.listTacheDyn = t;  }


	public int         getCptAfficheNiveau() { return this.cptAfficheNiveau;           }
	public List<Tache> getListTacheDyn()     { return this.listTacheDyn;               }
	public String      getDateManuelle()     { return this.textDateManuelle.getText(); }


	private Object getValeurColonne(Tache tache, int colonne) 
	{
		switch (colonne) 
		{
			case 0: return tache.getNom();
			case 1: return tache.getDuree();
			case 2: return getNomsTaches( tache.getPrc() );
			case 3: return getNomsTaches( tache.getSvt() );
			default: return "";
		}
	}


	// Méthode utilitaire pour obtenir les noms des tâches précédentes concaténés
	private String getNomsTaches( List<Tache> listeTaches )
	{
		StringBuilder sb;
		
		if ( listeTaches == null || listeTaches.isEmpty() ) { return ""; }
		
		sb = new StringBuilder();
		
		for ( int i = 0; i < listeTaches.size(); i++ )
		{
			sb.append( listeTaches.get(i).getNom() );
			
			if ( i < listeTaches.size() - 1 )
			{
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}


	/**
	* Méthode appelée lors d'une action utilisateur.
	* Gère différents événements comme le changement du type d'affichage, 
	* la navigation entre niveaux d'affichage, la sélection et sauvegarde de fichiers,
	* l'ajout, la suppression et la validation de tâches.
	*
	* @param e L'événement d'action déclenché par l'utilisateur.
	*/
	public void actionPerformed( ActionEvent e )
	{
		Mpm                         mpm;
		Tache                       t;
		
		String                      nomFichier;
		String[]                    tPrc;
		
		int                         tailleNom;
		int                         result;
		int                         ligneSelectionne;
		
		File                        selectedFile, fichier;
		
		JFileChooser                fc;
		DefaultTableModel           model;
		ArrayList<ArrayList<Tache>> tabNiveau;
		
		
		tabNiveau = this.ctrl.getMpm().getTabNiveau();
		mpm       = this.ctrl.getMpm();
		
		// gestion du du type d'affichage
		if ( e.getSource() == this.rbDteEntier  ){ this.ctrl.changerTypeAffichageDate("entier"); }
		
		// gestion du du type d'affichage
		if ( e.getSource() == this.rbDteManuelle){ this.ctrl.changerTypeAffichageDate("calendrier"); }
		
		// gestion du bouton + tot
		if ( e.getSource() == this.btnTot && this.cptAfficheNiveau < tabNiveau.size() - 1 )
		{
			if ( this.cptAfficheNiveau < tabNiveau.size() - 1 )
			{
				this.cptAfficheNiveau++;
				this.ctrl.changerTypeAffichageDate( "tot" );
				
				if ( this.cptAfficheNiveau == tabNiveau.size() -1 )
				{
					this.btnTot .setEnabled( false );
					this.btnTard.setEnabled( true  );
				}
			}
		}
		
		// gestion du bouton + tard
		if ( e.getSource() == this.btnTard && this.cptAfficheNiveau >= 0 )
		{
			cptAfficheNiveau--;
			this.ctrl.changerTypeAffichageDate( "lesDeux" );
			
			if( this.cptAfficheNiveau == 0 )
			{
				this.btnTard          .setEnabled( false );
				this.btnTot           .setEnabled( false );
				this.btnCheminCritique.setEnabled( true  );
			}
		}
		
		if ( e.getSource() == this.btnCheminCritique )
		{
			mpm.trouverCheminCritique();
			
			this.ctrl.appliquerCheminCritique();
		}
		
		// gestion du choix du fichier
		if ( e.getSource() == this.itemChoisirFichier )
		{
			fc = new JFileChooser( "../exemples/" );
			
			// Définir le titre de la boîte de dialogue
			fc.setDialogTitle( "Choisissez votre fichier" );
			
			// Affiche la boîte de dialogue et récupère le choix de l'utilisateur
			result = fc.showOpenDialog( this );
			
			// Vérification si l'utilisateur a choisi un fichier (et non annulé)
			if ( result == JFileChooser.APPROVE_OPTION )
			{
				// Récupération du fichier sélectionné par l'utilisateur
				selectedFile = fc.getSelectedFile();
				
				//Permet dans la condition "if" de verifier le type de fichier à lire
				nomFichier = selectedFile.getName();
				tailleNom = nomFichier.length(); 
				
				if (selectedFile.exists() && selectedFile.isFile() && tailleNom >= 5 && nomFichier.substring(tailleNom - 4, tailleNom).equals("data")  ) 
				{
					ctrl.setCheminFichier(selectedFile.getAbsolutePath());
					ctrl.ctrlInitMpm(selectedFile.getAbsolutePath());
				} 
				else
				{
					JOptionPane.showMessageDialog( this, "      Fichier invalide ! \nExtention valide: '.data' " );
				}
			}
		}
		
		
		if ( e.getSource() == this.itemEnregistrer )
		{
			model = this.tableModel;
			
			// Nouvel boite de dialogue
			fc = new JFileChooser( "../exemples/" );
			
			// Définir le titre de la boîte de dialogue
			fc.setDialogTitle( "Enregistrer sous" );
			
			result = fc.showSaveDialog( this );
			
			if ( result == JFileChooser.APPROVE_OPTION )
			{
				fichier = fc.getSelectedFile();
				
				// Ajoute automatiquement l'extension .txt si l'utilisateur ne l'a pas mise
				if ( ! fichier.getName().toLowerCase().endsWith(".data") )
				{
					fichier = new File( fichier.getAbsolutePath() + ".data" );
				}
				
				try ( FileWriter writer = new FileWriter( fichier ) )
				{
					for ( int i = 0; i < model.getRowCount(); i++ )
					{
						// Écrire les deux premières colonnes
						writer.write( model.getValueAt(i, 0).toString() + "|" );
						writer.write( model.getValueAt(i, 1).toString() + "|" );
						
						tPrc = model.getValueAt(i, 2).toString().split(",");
						
						// Écriture des tags séparés par des virgules
						for ( int j = 0; j < tPrc.length; j++ )
						{
							writer.write(tPrc[j] + ",");
						}
						
						writer.write( tPrc[tPrc.length -1] + "\n" );
					}
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
		
		// gestion du bouton Ajouter
		if ( e.getSource() == this.btnAjt )
		{
			t =  new Tache( "tache", 0, null );
			
			//Juste Pour l'affichage dans tableau
			tableModel.addRow(new Object[] { t.getNom(), t.getDuree(), getNomsTaches(t.getPrc()), getNomsTaches(t.getSvt()) }); // Exemple avec valeurs par défaut
		}
		else
		{
			if ( e.getSource() == this.btnEnl )
			{
				ligneSelectionne = table.getSelectedRow();
				
				if ( ligneSelectionne != -1 )
					tableModel.removeRow( ligneSelectionne );
			}
		}
		
		// gestion du bouton Valider
		if ( e.getSource() == this.btnValid )
		{
			this.creerNouveauTabTache();
			
			this.ctrl.setAfTaDynamique( true );
			this.tableModel.setRowCount(0);
			this.ctrl.ctrlInitMpm( this.ctrl.getCheminFichier() );
		}
	}



	/**
	* Méthode appelée lors d'une modification dans le modèle de données du tableau.
	* Elle détecte la cellule modifiée, valide les données saisies (nom, durée, précédents, suivants).
	* Et met à jour les tâches correspondantes dans le modèle métier, et affiche un message d'erreur si la saisie est incorrecte.
	*
	* @param e L'événement de changement de modèle de table contenant les détails des modifications.
	*/

	
	public void tableChanged( TableModelEvent e )
	{
		Mpm     mpm;
		Tache   tache;
		Tache   svt ;
		Tache   prc;
		
		int     premierligne;
		int     dernièreLigne;
		int     colonne;
		int     count;
		String  messageErreur;
		String  newValue;
		
		List<Tache>      toutesLesTaches;
		ArrayList<Tache> nouveauxPrecedents;
		ArrayList<Tache> nouveauxSuivants;
		
		Object rawValue;

		svt = null;
		prc = null;
		
		
		premierligne   = e.getFirstRow();
		dernièreLigne  = e.getLastRow();
		colonne        = e.getColumn();
		// Si la colonne ou les lignes ne sont pas valides, on ignore
		if ( colonne < 0 || premierligne < 0 || dernièreLigne < 0 ) return;
		
		mpm = this.ctrl.getMpm();
		toutesLesTaches = mpm.getTaches();
		
		for ( int ligne = premierligne; ligne <= dernièreLigne; ligne++ )
		{
			// Trouver la tâche correspondant à la ligne visible en sautant "Début" et "Fin"
			count = -1;
			tache = null;
			for ( Tache t : toutesLesTaches )
			{
				if ( t.getNom().equals("Début") || t.getNom().equals("Fin") ) {continue;}
				count++;
				if ( count == ligne )
				{
					tache = t;
					break;
				}
			}
			
			if ( tache == null ) continue; // sécurité
			
			// Vérifie que la case n'est pas vide
			rawValue = this.tableModel.getValueAt(ligne, colonne);
			newValue = rawValue.toString().trim();
			
			if ( newValue.isEmpty() )
			{
				JOptionPane.showMessageDialog( null, "Le champ ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE );
				this.tableModel.setValueAt( getValeurColonne(tache, colonne), ligne, colonne );
				continue;
			}
			
			try
			{
				switch ( colonne )
				{
					case 0: // Nom
						tache.setNom( newValue );
						break;
					
					case 1: // Durée
						int duree = Integer.parseInt( newValue );
						if ( duree < 0 )
						{
							throw new NumberFormatException( "La durée ne peut pas être négative." );
						}
						else
						{
							tache.setDuree(Integer.parseInt(newValue));
						}
						break;
					
					case 2: // Précédents
						nouveauxPrecedents = new ArrayList<Tache>();
						for ( String nom : newValue.split(",") )
						{
							prc = mpm.getTacheParNom(nom.trim());
							if ( prc != null )
								nouveauxPrecedents.add(prc);
							else
								throw new Exception( "Tâche précédente introuvable : " + nom );
						}
						
						tache.setTachePrc( nouveauxPrecedents );
						break;
					
					case 3: // Suivants
						nouveauxSuivants = new ArrayList<Tache>();
						for ( String nom : newValue.split(",") )
						{
							svt = mpm.getTacheParNom(nom.trim());
							if ( svt != null )
								nouveauxSuivants.add(svt);
							//else
								//throw new Exception("Tâche suivante introuvable : " + nom);
						}
						
						tache.setTacheSvt( nouveauxSuivants );
						break;
				}

                
			}
			catch ( Exception ex )
			{
				// Personalisationde de l'erreur de mettre un Strin dans une case qui attend un int
				if ( ex.getMessage().contains( "For input string" ) )
				{
					messageErreur = "La durée doit être un entier positif (ex : 3, 7, 14)." + "\n" + "Les chaînes de caractères ou caractères ne sont pas autorisées.";
				}
				else 
				{
					messageErreur = ex.getMessage();
				}
				
				JOptionPane.showMessageDialog(this,  messageErreur , "Erreur" , JOptionPane.ERROR_MESSAGE);
				this.tableModel.setValueAt(getValeurColonne(tache, colonne), ligne, colonne);
			}
		}
		
		this.ctrl.repaintPanelMpm();
	}


	/**
	 * Méthode qui crée une nouvelle liste de tâches à partir des données d'un DefaultTableModel.
	 * Elle lit les noms, durées, précédents et suivants depuis le tableau,
	 * puis crée les objets Tache correspondants avec leurs dépendances correctement liées.
	 */
	public void creerNouveauTabTache()
	{
		Tache    tache;
		Tache    tacheCourante;
		
		int      duree;
		String   nom;
		String   prcStr;
		String   svtStr;
		String[] nomsPrec;
		String[] nomsSvt;
		
		ArrayList<Tache>  listeTaches;
		DefaultTableModel model;
		
		
		this.listTacheDyn.clear();
		
		model = this.tableModel;
		listeTaches = new ArrayList<Tache>();
		
		for ( int i = 0; i < model.getRowCount(); i++ )
		{
			nom  = (String) model.getValueAt(i, 0); // nom
			duree   = Integer.parseInt(model.getValueAt(i, 1).toString()); // durée
			
			tache = new Tache(nom, duree, new ArrayList<Tache>());
			listeTaches.add(tache);
		}
		
		for ( int i = 0; i < model.getRowCount(); i++ )
		{
			prcStr = (String) model.getValueAt(i, 2);
			svtStr = (String) model.getValueAt(i, 3);
			
			tacheCourante = listeTaches.get(i);
			
			if ( prcStr != null && !prcStr.isBlank() ) //isBlank verifie si il ni a pas de blanc ds la chaine ou est vide
			{
				nomsPrec = prcStr.split(",");
				for ( String nomPrec : nomsPrec )
				{
					for (Tache t : listeTaches) 
					{
						if ( t.getNom().equals(nomPrec.trim() ) ) //J'ajoute .trim pour supprimer les blanc possible de l'utilisateur a pus entré en debut et fin de String
						{
							tacheCourante.ajouterTachePrc(t);
							t.ajouterTacheSvt(tacheCourante);
						}
					}
				}
			}
			
			if ( svtStr != null && !svtStr.isBlank() )
			{
				nomsSvt = svtStr.split(",");
				for ( String nomSvt : nomsSvt )
				{
					for ( Tache t : listeTaches )
					{
						if ( t.getNom().equals( nomSvt.trim() ) )
						{
							tacheCourante.ajouterTacheSvt(t);
							t.ajouterTachePrc(tacheCourante);
						}
					}
				}
			}
		}
		
		// Mise à jour de la liste de tâches dynamique
		this.setListTacheDyn( listeTaches );
	}


	/**
	* Construit et retourne la barre de menu principale de l'application.
	* Cette méthode doit être appelée depuis la Frame principale **après**
	* que tous les composants nécessaires ont été initialisés, afin d'éviter
	* un "NullPointerException".
	* 
	* @return la JMenuBar construite avec les menus ajoutés.
	*/
	public JMenuBar construireMenuBar()
	{
		this.menuBar.add( menuFichier );
		
		return menuBar;
	}
}
