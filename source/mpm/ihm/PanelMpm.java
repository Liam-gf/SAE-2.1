/**
* Classe PanelMpm : Permet d'afficher le graphe 
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.ihm;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import mpm.*;
import mpm.metier.*;


public class PanelMpm extends JPanel
{
	private FrameMpm                  frameMpm;
	
	private TacheGraphique            tacheGraphique;
	
	private Controleur                ctrl;
	private CheminCritique            cheminCritique;
	
	private ArrayList<TacheGraphique> lstTG;
	
	// Pour déplacer une tâche avec la souris
	//private TacheGraphique tacheSelectionnee;
	private int                       sourisX, sourisY;
	
	//Sert pour la maj de l'affichage des clic bouton
	private String  typeAffichageDate; // "nul", "tot", "tard"
	private boolean afficherDateTot ;
	private boolean afficherDateTard;
	private boolean afficherDatesCalendrier;
	
	private int     cptNivDateTot;
	

	/**
	* Constructeur du Panel MPM.
	* Initialise les composants graphiques et configure la taille en fonction des tâches graphiques à afficher.
	*
	* @param ctrl Le contrôleur principal de l'application.
	* @param frameMpm La fenêtre principale contenant ce Panel.
	*/
	public PanelMpm( Controleur ctrl, FrameMpm frameMpm )
	{
		int        maxLargeur;
		int        maxHauteur;
		int        largeurTotale;
		int        hauteurTotale;
		GereSouris gestionnaireSouris;
		
		this.ctrl             = ctrl;
		this.frameMpm         = frameMpm;
		
		this.cheminCritique   = null;
		this.lstTG            = new ArrayList<TacheGraphique>();
		
		this.afficherDateTot  = true;
		this.afficherDateTard = false;
		
		this.afficherDatesCalendrier = false;
		
		this.cptNivDateTot  = 0;
		
		// Bordure rouge pour voir les limites réelles du panel
		this.setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
		
		this.setBackground( new Color( 0xfff3f3  ));
		
		/* ------------------------------ */
		/* Création des composants        */
		/* ------------------------------ */
		
		// On régénère les tâches graphiques si un nouveau graphe et generé en passan par maj dan Frame
		if ( ctrl.estNouveauGraphe() == true )
		{
			creerTacheGraphique();
			ctrl.setNouveauGraphe( false );
		}
		//this.ctrl.getMpm().creerNiveau();
		
		maxLargeur = 0;
		for ( TacheGraphique tg : lstTG )
		{
			if ( tg.getLargeurTG() > maxLargeur )
			{
				maxLargeur = tg.getLargeurTG();
			}
		}
		
		maxHauteur = 0;
		for ( TacheGraphique tg : lstTG )
		{
			if ( tg.getHauteurTG() > maxHauteur )
			{
				maxHauteur = tg.getHauteurTG();
			}
		}
		
		largeurTotale = ctrl.getMpm().getNombreNiveau() * ( maxLargeur );
		hauteurTotale = ctrl.getMpm().getMaxNiveau()    * ( maxHauteur );
		
		this.setPreferredSize(new Dimension(largeurTotale, hauteurTotale));
		
		this.revalidate();
		
		/* ------------------------------ */
		/* Activation des composants      */
		/* ------------------------------ */
		this.typeAffichageDate = "tot";
		
		repaint();
		
		gestionnaireSouris = new GereSouris(this.ctrl);
		this.addMouseListener(gestionnaireSouris);
		this.addMouseMotionListener(gestionnaireSouris);
		ToolTipManager.sharedInstance().registerComponent(this);
	}


	/**
	* Définit le chemin critique à afficher dans le panneau.
	*
	* @param cc Le chemin critique à afficher.
	*/
	public void setCheminCritique( CheminCritique cc )
	{
		this.cheminCritique = cc;
	}


	/**
	* Définit et traite le type d'affichage des dates dans le panneau.
	* Peut afficher les dates totales, tardives, les deux, ou un calendrier.
	*
	* @param type Le type d'affichage ("tot", "lesDeux", "entier", "calendrier").
	*/
	public void setTypeAffichageDate( String type )
	{
		switch( type )
		{
			case "tot":
				afficherDatesTotales();
				this.cptNivDateTot =  ctrl.getFrameMpm().getPanelAction().getCptAfficheNiveau();
				break;
			
			case "lesDeux":
				afficherLesDeuxDates();
				break;
			
			case "entier":
				afficherDatesEntier();
				break;
			
			case "calendrier":
				afficherDatesCalendrier();
				break;
			
			default:
				afficherDateTot  = false;
				afficherDateTard = false;
				repaint();
		}
	}


	/**
	* Calcule et retourne la taille préférée du panneau en fonction
	* des dimensions et positions des tâches graphiques affichées.
	* Ajoute une marge supplémentaire pour éviter que les tâches
	* ne soient trop proches des bords.
	*
	* @return La dimension dynamique du Panel.
	*/
	@Override
	public Dimension getPreferredSize()
	{
		int maxLargeur, maxHauteur;
		int droite, bas;
		
		maxLargeur = 0;
		maxHauteur = 0;
		
		// Calcule la largeur et hauteur max de toutes les tâches graphiques
		for ( TacheGraphique tg : lstTG )
		{
			droite  = tg.getCoorTacheGraphX() + tg.getLargeurTG();
			bas     = tg.getCoorTacheGraphY() + tg.getHauteurTG();
			
			if ( droite > maxLargeur ) { maxLargeur = droite; }
			if ( bas    > maxHauteur ) { maxHauteur = bas;    }
		}
		
		//Ajout d'un marge pour plus clarter
		maxLargeur += 100;
		maxHauteur += 100;
		
		return new Dimension( maxLargeur, maxHauteur );
	}


	/**
	* Surcharge la méthode paintComponent pour dessiner les tâches graphiques,
	* leurs dates (totales et tardives) et les flèches représentant les dépendances
	* entre tâches. Affiche également en rouge les flèches du chemin critique si défini.
	*
	* @param g Le contexte graphique utilisé pour le dessin.
	*/
	@Override
	protected void paintComponent(Graphics g)
	{
		Tache            tDest;
		Tache            tSource;
		
		TacheGraphique   tgSource;
		TacheGraphique   tgDest;
		TacheGraphique   tgCible;
		
		int              nbNiveaux;
		
		ArrayList<Tache> tabNiv;
		ArrayList<Tache> niveau;
		
		Point            sortie;
		Point            entree;
		
		Graphics2D       g2;
		StringBuilder    sb;
		
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		
		// Affichage de la boîte + nom + traits
		for ( TacheGraphique tg  : lstTG )
			tg.dessiner(g2);
		
		// Affichage des dates Tard
		if ( this.afficherDateTard )
		{
			nbNiveaux = ctrl.getMpm().getNombreNiveau();
			
			for ( int i = nbNiveaux - 1; i >= ctrl.getFrameMpm().getPanelAction().getCptAfficheNiveau() ; i-- )
			{
				tabNiv = ctrl.getMpm().getTabNiveau().get(i);
				
				for ( Tache tabN : tabNiv )
				{
					for ( TacheGraphique tg : lstTG )
					{
						if ( tabN.getNom().equals( tg.getTacheGraphique().getNom() ) )
						{
							tg.afficherDateTard( g2,this.afficherDatesCalendrier );
							
							if ( this.afficherDatesCalendrier )
								tg.afficherDateCalendier( g, this.afficherDatesCalendrier, "tard");
						}
					}
				}
			}
		}
		
		// Affichage des dates tots
		if ( this.afficherDateTot )
		{
			for ( int i = 0; i <= this.cptNivDateTot; i++ )
			{
				niveau = ctrl.getMpm().getTabNiveau().get(i);
				for ( Tache t : niveau )
				{
					for ( TacheGraphique tg : this.lstTG )
					{
						if ( tg.getTacheGraphique().equals(t) )
						{
							if( t.getNom().equals( "Fin" ) )
								tg.afficherDateTard( g2,this.afficherDatesCalendrier );
							
							tg.afficherDateTot( g2,this.afficherDatesCalendrier );
							
							if ( this.afficherDatesCalendrier )
								tg.afficherDateCalendier( g, this.afficherDatesCalendrier, "tot");
						}
					}
				}
			}
		}
		
		// Dessiner les flèches selon les dépendances
		for ( TacheGraphique tGSource : lstTG )
		{
			tSource = tGSource.getTacheGraphique();
			
			for ( Tache tSuivante : tSource.getSvt() )
			{
				tgCible = trouverTGDepuisTache( tSuivante );
				if ( tgCible != null )
				{
					sortie = tGSource.getSortieDroite();
					entree = tgCible.getEntreeGauche();
					dessinerFleche( g2, sortie, entree, tSource, false );
				}
			}
		}
		
		// Dessiner les flèches du chemin critique selon les dépendances
		if ( this.cheminCritique != null )
		{
			for ( ArrayList<Tache> chemin : this.cheminCritique.getChemin() )
			{
				sb = new StringBuilder( "Chemin critique : " );
				
				for ( int i = chemin.size() - 1; i >= 0; i-- )
				{
					sb.append(chemin.get(i).getNom());
					if (i > 0) sb.append(" -> ");
				}
				
				// DESSIN des flèches rouges
				for ( int i = chemin.size() - 1; i > 0; i-- )
				{
					tSource = chemin.get(i);
					tDest   = chemin.get(i - 1);
					
					tgSource = trouverTGDepuisTache( tSource );
					tgDest   = trouverTGDepuisTache( tDest   );
					
					if ( tgSource != null && tgDest != null )
					{
						sortie = tgSource.getSortieDroite();
						entree = tgDest.getEntreeGauche();
						
						dessinerFleche( (Graphics2D) g, sortie, entree, tSource, true );
					}
					// sinon, on ne fait rien (pas besoin de continue)
				}
			}
		}
	}


	/**
	* Dessine une flèche entre deux points représentant une dépendance entre tâches,
	* avec une durée affichée au centre. La flèche est rouge si "Chemin Critique", bleue sinon.
	*
	* @param g2 Le contexte graphique 2D pour dessiner.
	* @param from Point de départ de la flèche.
	* @param to Point d'arrivée de la flèche.
	* @param tSource Tâche source dont la durée sera affichée.
	* @param estCritique Indique si la flèche représente une dépendance critique (rouge).
	*/
	private void dessinerFleche( Graphics2D g2, Point from, Point to, Tache tSource, boolean estCritique )
	{
		String      duree;
		FontMetrics fm;
		
		double      espace, espaceDebut, espaceFin;
		double      angle, distance;
		
		int         xEspaceDebut, yEspaceDebut, xEspaceFin, yEspaceFin;
		int         taillefleche;
		int         x1, x2, y1, y2;
		int         midX, midY;
		int         textLargeur, textLongueur;
		
		g2.setStroke( new BasicStroke(2) );
		
		if ( estCritique ) { g2.setColor( Color.RED ); }
		else { g2.setColor( Color.BLUE ); }
		
		// Calcul de l'angle de la flèche
		angle    = Math.atan2( to.y - from.y, to.x - from.x );
		distance = from.distance(to);
		
		// Texte à afficher
		duree        = String.valueOf(tSource.getDuree());
		fm           = g2.getFontMetrics();
		textLargeur  = fm.stringWidth(duree);
		textLongueur = fm.getAscent();
		
		// Longueur à laisser vide au centre
		espace = textLargeur + 10; // 10px de marge autour du texte
		
		// Position du point avant le texte
		espaceDebut   = ( distance - espace ) / 2;
		xEspaceDebut  = (int) ( from.x + espaceDebut * Math.cos( angle ) );
		yEspaceDebut  = (int) ( from.y + espaceDebut * Math.sin( angle ) );
		
		// Position du point après le texte
		espaceFin  = ( distance + espace ) / 2;
		xEspaceFin = (int) ( from.x + espaceFin * Math.cos( angle ) );
		yEspaceFin = (int) ( from.y + espaceFin * Math.sin( angle ) );
		
		// Dessiner les deux segments de la flèche (avant et après le texte)
		g2.drawLine( from.x, from.y, xEspaceDebut, yEspaceDebut );
		g2.drawLine( xEspaceFin, yEspaceFin, to.x, to.y );
		
		// Dessiner la tête de flèche
		taillefleche = 6;
		x1 = (int) ( to.x - taillefleche * Math.cos( angle - Math.PI / 6 ) );
		y1 = (int) ( to.y - taillefleche * Math.sin( angle - Math.PI / 6 ) );
		x2 = (int) ( to.x - taillefleche * Math.cos( angle + Math.PI / 6 ) );
		y2 = (int) ( to.y - taillefleche * Math.sin( angle + Math.PI / 6 ) );
		g2.drawLine( to.x, to.y, x1, y1 );
		g2.drawLine( to.x, to.y, x2, y2 );
		
		// Dessiner la durée au centre
		midX = ( from.x + to.x ) / 2;
		midY = ( from.y + to.y ) / 2;
		g2.setColor( Color.RED );
		g2.drawString( duree, midX - textLargeur / 2, midY + textLargeur / 2 - 2 );
	}


	/**
	* Gestionnaire d'événements souris pour gérer la sélection, le déplacement et 
	* l'affichage d'informations sur les tâches graphiques dans le panneau MPM.
	*/
	private class GereSouris extends MouseAdapter
	{
		private Controleur     ctrl;
		private TacheGraphique tacheSelectionnee; // tâche sélectionnée pour drag
		private int            sourisX, sourisY;  // position souris au dernier événement
		
		    
		/**
		* Constructeur GereSouris
		* @param ctrl Contrôleur principal de gestion.
		*/
		public GereSouris( Controleur ctrl )
		{
			this.ctrl = ctrl;
		}
		
		/**
		* Méthode appelée lors du clic souris.
		* Sélectionne la tâche graphique sous le pointeur, si elle existe.
		* @param e L'événement souris.
		*/
		public void mousePressed( MouseEvent e )
		{
			sourisX = e.getX();
			sourisY = e.getY();
			
			// Vérifie si un clic est fait sur une tâche
			for ( TacheGraphique tg : lstTG )
			{
				if ( tg.getCoorTacheGraphX() <= sourisX && sourisX <= tg.getCoorTacheGraphX() + tg.getLargeurTG() &&
					 tg.getCoorTacheGraphY() <= sourisY && sourisY <= tg.getCoorTacheGraphY() + tg.getHauteurTG()    )
				{
					tacheSelectionnee = tg; // assigne la tâche sélectionnée
					break;
				}
			}
		}
		
		
		/**
		* Méthode appelée lors du déplacement de la souris avec bouton enfoncé.
		* Déplace la tâche graphique sélectionnée selon le déplacement du curseur.
		* Les déplacements sont limités aux dimensions du Panel.
		* @param e L'événement souris.
		*/
		public void mouseDragged( MouseEvent e )
		{
			int dx, dy;
			int newX, newY;
			
			if ( tacheSelectionnee != null )
			{
				dx = e.getX() - sourisX;
				dy = e.getY() - sourisY;
				
				newX = tacheSelectionnee.getCoorTacheGraphX() + dx;
				newY = tacheSelectionnee.getCoorTacheGraphY() + dy;
				
				// Limites de déplacement
				newX = Math.max( 0, Math.min( newX, PanelMpm.this.getWidth()  - tacheSelectionnee.getLargeurTG() ) );
				newY = Math.max( 0, Math.min( newY, PanelMpm.this.getHeight() - tacheSelectionnee.getHauteurTG() ) );
				
				tacheSelectionnee.setCoorTacheGraphX( newX );
				tacheSelectionnee.setCoorTacheGraphY( newY );
				
				sourisX = e.getX();
				sourisY = e.getY();
				
				repaint();
			}
		}
		
		
		/**
		* Méthode appelée lors du relâchement du bouton souris.
		* Libère la tâche sélectionnée.
		* @param e L'événement souris.
		*/
		public void mouseReleased( MouseEvent e )
		{
			tacheSelectionnee = null; // relâche la tâche sélectionnée
		}
		
		
		/**
		* Méthode appelée lors du déplacement de la souris sans bouton enfoncé.
		* Affiche une info-bulle expliquant les calculs 'Tot' et 'Tard' pour la tâche
		* survolée, si applicable.
		* @param e L'événement souris.
		*/
		public void mouseMoved( MouseEvent e )
		{
			Tache   tachePrc, tacheSvt;
			
			String  message;
			String  nomTacheActuelle;
			
			int     sourisX, sourisY;
			boolean surTache;
			
			
			sourisX = e.getX();
			sourisY = e.getY();
			
			surTache = false;
			message = "";
			
			//Pour afficher le PopUp de Tache Graphique TOT
			for ( TacheGraphique tg : lstTG )
			{
				nomTacheActuelle = tg.getTacheGraphique().getNom();
				
				// Survol zone "Tot" (moitié gauche de la tâche)
				if ( sourisX >= tg.getCoorTacheGraphX()                            &&
				     sourisX <  tg.getCoorTacheGraphX() + ( tg.getLargeurTG() /2 ) &&
				     sourisY <= tg.getCoorTacheGraphY() +   tg.getHauteurTG()      &&
				     sourisY >= tg.getCoorTacheGraphY() + ( tg.getHauteurTG() /2 ) &&
				                tg.getDateTot() != -1                                    )
				{
					tachePrc = tg.getTacheGraphique().getTachePrcMaxDateTot();
					
					message = "<html><b><u>Calcul de la date au plus tôt :</u></b><br>" 
									+ "<b>Tâche actuelle :</b> " + "<b style='color: red;'>" +nomTacheActuelle + "</b><br><br>";
					
					if( tachePrc != null )
					{
						message += "Pour chaque tâche précédente :<br>" 
								+ "<b style='color: green;'>Durée Totale = Date de début tache prc + durée</b><br><br>"    
								+ "            <=> " + "<b style='color: green;'>" + tachePrc.getDateTot() + " + " +  "<b style='color: red;'>" + tachePrc.getDuree() + "<br><br>" 
								
								
								+ "<b><u>Résultat</u></b>"
								+ "<b style='color: green;'> parmi les 'Durées Totales' calculées, la tâche prc avec la plus grande Durée Totale est :  </b>"
								+ "<b style='color: red;'>" + tg.getTacheGraphique().getTachePrcMaxDateTot().getNom() 
								+ "</b></html>";
					}
					else
					{
						message += "<b style='color: green;'>Pour tâche : </b><b style='color: red;'>" + nomTacheActuelle 
								+ "</b> <b style='color: green;'>il n'y a pas de calcul à faire pour date 'Tot' !</b><br><br>"
								+  "<b style='color: red;'> Pourquoi ? </b><br>"
								+  "Car c'est juste une tâche fictive qui marque le début du projet. <br>" 
								+  "La date 'Tot' est donc forcément la date <b>Actuelle du début du projet ou est à 0</b>" ;
					}
					
					PanelMpm.this.setToolTipText(message);
					ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
					
					surTache = true;
					break;
				}
				
				 // Survol zone "Tard" (moitié droite de la tâche)
				if ( sourisX <= tg.getCoorTacheGraphX() +   tg.getLargeurTG()       &&
				     sourisX  > tg.getCoorTacheGraphX() + ( tg.getLargeurTG() / 2 ) &&
				     sourisY <= tg.getCoorTacheGraphY() +   tg.getHauteurTG()       &&
				     sourisY >= tg.getCoorTacheGraphY() + ( tg.getHauteurTG() / 2 ) &&
				                tg.getDateTard() != -1                                  )
				{
					tacheSvt = tg.getTacheGraphique().getTacheTardSvtPlusPetit();
					
					message = "<html><b><u>Calcul de la date au plus 'Tard' :</u></b><br>" 
								+ "<b>Tâche actuelle :</b> " + "<b style='color: red;'>" + nomTacheActuelle + "</b><br><br>" ;
					
					if ( tacheSvt != null )
					{
						message +=  "Pour chaque tâche suivante :<br>" 
									+ "<b style='color: green;'>Durée Totale Tard = Date 'Tard' de tâche svt - durée de tâche </b>"
									+ "<b style='color: red;'>" + nomTacheActuelle + "</b><br><br>"    
									+ "            <=> " + "<b style='color: red;'>" + tacheSvt.getDateTard() + " - " +  "<b style='color: green;'>" + tg.getTacheGraphique().getDuree() + "<br><br>" 
									
									
									+ "<b style='color: red;'><u>Résultat</u></b>"
									+ "<b style='color: green;'> parmi les 'Durées Totales Tard' calculées, la tâche svt avec la plus PETITE 'Durée totale tard' est :  </b>"
									+ "<b style='color: red;'>" + tacheSvt.getNom() + "</b></b></html>";
					}
					else
					{
						message +=  "Pour tâche : " + nomTacheActuelle + " le calcule est :<br>" 
									+ "<b style='color: green;'>Durée Totale Tard = Date tot de la tâche </b>" 
									+ "<b style='color: red;'>" + nomTacheActuelle + "</b><br><br>"    
									+ "            <=> " + "<b style='color: green;'>" + tg.getTacheGraphique().getDateTot() + "</b><br><br>" 
									
									
									+ "<b style='color: red;'> <u>Résultat</u>: " + nomTacheActuelle + "</b>"
									+ "<b style='color: green;'> n'ayant pas de suivant elle prend en date 'Tard' sa date 'Tot'"
									+ "</b></html>";
					}
					
					PanelMpm.this.setToolTipText( message );
					ToolTipManager.sharedInstance().setDismissDelay( Integer.MAX_VALUE );
					
					surTache = true;
					break;
				}
			}
			
			if ( ! surTache )
			{ 
				PanelMpm.this.setToolTipText( null );
				ToolTipManager.sharedInstance().setDismissDelay(0);
			}
		}
	}


	/**
	* Recherche dans la liste des tâches graphiques celle qui correspond à la tâche donnée en paramètre.
	* 
	* Parcourt la liste lstTG et compare chaque tâche graphique à la tâche t via la méthode equals.
	* Si une correspondance est trouvée, la tâche graphique est retournée.
	* Sinon, la méthode renvoie null.
	* 
	* @param t la tâche métier à partir de laquelle on veut obtenir la tâche graphique associée
	* @return la tâche graphique correspondant à la tâche t, ou null si aucune correspondance
	*/
	public  TacheGraphique trouverTGDepuisTache( Tache t )
	{
		for ( TacheGraphique tg : lstTG )
		{
			if ( tg.getTacheGraphique().equals(t) )
			{
				return tg;
			}
		}
		
		return null;
	}


	/**
	* Crée la représentation graphique des tâches en les positionnant dans la fenêtre.
	* 
	* Cette méthode parcourt les niveaux de tâches (groupes de tâches au même niveau du graphe) et crée
	* pour chaque tâche un objet graphique (TacheGraphique) avec une position X et Y calculée.
	* 
	* Les tâches d'un même niveau sont espacées verticalement de façon équitable pour remplir la hauteur de la fenêtre,
	* tandis que les niveaux sont espacés horizontalement selon la largeur maximale des tâches créées précédemment.
	* 
	* Cette méthode vide d'abord la liste des tâches graphiques avant de recréer toutes les tâches graphiques.
	*/
	public void creerTacheGraphique()
	{
		int cpt ;
		int taille;
		int hauteurZone;
		int ecartement;
		int yDepart;
		int posY;
		int maxLargeur;
		
		cpt = 50;
		
		lstTG.clear();
		
		for ( ArrayList<Tache> t : this.ctrl.getMpm().getTabNiveau() )
		{
			taille = t.size();
			
			if ( taille > 1 )
			{
				// Espace vertical total disponible
				hauteurZone = ctrl.getHautFenetre();
				
				// Calcul de l'espacement entre les tâches de ce niveau
				ecartement = hauteurZone / ( taille + 1 ); // +1 pour équilibrer l'espacement en haut/bas
				
				// Point de départ vertical pour centrer le bloc entier sur la fenêtre
				yDepart = ( ctrl.getHautFenetre() / 2 ) - ( ( taille - 1 ) * ecartement / 2 );
				
				for ( int i = 0; i < taille; i++ )
				{
					posY = yDepart + ( i * ecartement );
					this.lstTG.add( new TacheGraphique( t.get(i), cpt, posY , this.frameMpm ) ); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				}
			}
			else
			{
				this.lstTG.add( new TacheGraphique( t.get(0), cpt, ( ctrl.getHautFenetre()/2 ), this.frameMpm ) );
			}
			
			maxLargeur = 0;
			
			for ( TacheGraphique tg : this.lstTG )
			{
				if ( tg.getLargeurTG() > maxLargeur )
				{
					maxLargeur = tg.getLargeurTG();
				}
			}
			
			cpt += maxLargeur + 100;
		}
	}


	/**
	* Désactive l'affichage des dates au format calendrier.
	* 
	* Cette méthode définit le booléen afficherDatesCalendrier à false,
	* ce qui peut être utilisé dans la méthode de dessin pour afficher les dates autrement
	* (par exemple en nombre de jours depuis le début).
	* Ensuite, elle redessine l'interface avec repaint().
	*/
	public void afficherDatesEntier()
	{
		this.afficherDatesCalendrier = false;
		repaint();
	}

	/**
	* Active l'affichage des dates au format calendrier (JJ/MM ou similaire).
	* 
	* En définissant le booléen afficherDatesCalendrier à true,
	* cette méthode permet à la vue d'afficher les dates dans un format lisible
	* comme une vraie date de calendrier. L'interface est redessinée ensuite.
	*/
	public void afficherDatesCalendrier()
	{
		this.afficherDatesCalendrier = true;
		repaint();
	}


	/**
	* Active uniquement l'affichage des dates au plus tôt.
	* 
	* Cette méthode permet de ne montrer que les dates les plus précoces
	* pour chaque tâche (date au plus tôt), en désactivant l'affichage
	* des dates au plus tard.
	*/
	public void afficherDatesTotales()
	{
		this.afficherDateTot  = true;
		this.afficherDateTard = false;
		repaint();
	}

	/**
	* Active l'affichage des deux types de dates : au plus tôt et au plus tard.
	* 
	* Cela permet à l’utilisateur de comparer les deux valeurs directement
	* dans la même vue, ce qui est utile pour visualiser les marges temporelles
	* et identifier le chemin critique.
	*/
	public void afficherLesDeuxDates()
	{
		this.afficherDateTot  = true;
		this.afficherDateTard = true;
		repaint();
	}
}
