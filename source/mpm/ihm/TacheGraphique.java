/**
* Classe TacheGraphique : Permet de mettre les taches de manière graphique 
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.ihm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.GregorianCalendar;
import mpm.metier.Tache;

public class TacheGraphique
{
	private FrameMpm    frame;
	private Tache       tache;
	private int         coordX;
	private int         coordY;
	
	private int         dateTot;
	private int         dateTard;
	
	private int         hauteur;
	private int         largeur;
	
	private int         positionXLigne;
	private int         positionYLigne;
	
	private FontMetrics fm;
	
	/**
	* Constructeur de la classe TacheGraphique.
	* 
	* Ce constructeur initialise une tâche graphique à afficher dans l’interface.
	* Il positionne la tâche aux coordonnées données (x, y), et ajuste dynamiquement
	* sa largeur en fonction de la taille du texte et des dates à afficher,
	* pour s’assurer que tout soit lisible.
	* 
	* @param t      La tâche associée (modèle logique).
	* @param x      La position horizontale de la tâche graphique.
	* @param y      La position verticale de la tâche graphique.
	* @param frame  La fenêtre principale (FrameMpm), utilisée pour l’intégration graphique.
	*/
	public TacheGraphique( Tache t, int x, int y, FrameMpm frame )
	{
		// création des différents variables
		BufferedImage img;
		Graphics2D g2;
		String exempleDate;
		
		int largeurTexte;
		int largeurDate ;
		int largeurExempleDate;
		
		// initialiation des différents variables
		this.frame  = frame;
		
		this.tache  = t;
		
		this.coordX = x;
		this.coordY = y;
		
		this.dateTot  = -1;
		this.dateTard = -1;
		
		this.largeur = 75;
		this.hauteur = 75;
		
		// calcul la largeur de la boite en fonction de la tache
		img = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
		
		g2 = img.createGraphics();
		
		g2.setFont( new Font( "SansSerif", Font.BOLD, 14 ) );
		
		this.fm = g2.getFontMetrics();
		
		exempleDate = "00/00/00";
		
		largeurExempleDate = fm.stringWidth( exempleDate );
		
		largeurTexte = fm.stringWidth( t.getNom() );
		largeurDate  = this.fm.stringWidth( String.valueOf( this.tache.getDateTot() ) );
		
		if ( largeurDate < largeurExempleDate )
		{
			largeurDate = largeurExempleDate;
		}
		
		if ( largeurDate < this.fm.stringWidth( String.valueOf( this.tache.getDateTard() ) ) )
		{
			largeurDate = this.fm.stringWidth( String.valueOf( this.tache.getDateTard() ) );
		}
		
		if ( largeurTexte > this.largeur )     { this.largeur = largeurTexte +  20; }
		if ( largeurDate  > this.largeur / 2 ) { this.largeur = largeurDate  + 120; }
		
		this.positionXLigne = this.positionYLigne = 0;
	}


	// Les différent setteurs
	public void setCoorTacheGraphX( int X ) { this.coordX  = X; }
	public void setCoorTacheGraphY( int Y ) { this.coordY  = Y; }
	public void setLargeurTG      ( int l ) { this.largeur = l; }
	public void setHauteurTG      ( int h ) { this.hauteur = h; }


	// Les différents guetteurs
	public Tache getTacheGraphique()  { return this.tache;    }
	public int   getCoorTacheGraphX() { return this.coordX;   }
	public int   getCoorTacheGraphY() { return this.coordY;   }
	public int   getDateTot()         { return this.dateTot ; }
	public int   getDateTard()        { return this.dateTard; }
	public int   getLargeurTG()       { return this.largeur;  }
	public int   getHauteurTG()       { return this.hauteur;  }
	
	public Point getCentre()       { return new Point( this.coordX + largeur / 2, this.coordY + this.hauteur / 2 ); }
	public Point getSortieDroite() { return new Point( this.coordX + largeur    , this.coordY + this.hauteur / 2 ); }
	public Point getEntreeGauche() { return new Point( this.coordX              , this.coordY + this.hauteur / 2 ); }


	/**
	* Méthode responsable de dessiner graphiquement la tâche sur l'écran.
	* 
	* Elle trace une boîte contenant le nom de la tâche, ainsi que des lignes
	* de séparation pour afficher d'autres informations comme les dates.
	* Le texte est centré horizontalement, et les lignes intérieures
	* permettent de structurer l'affichage.
	* 
	* Le dessin utilise lissage (antialiasing) pour éviter les effets en escalier.
	*
	* @param g L’objet Graphics fourni par Swing pour dessiner sur le composant.
	*/
	public void dessiner( Graphics g )
	{
		String nom;
		int    largeurNom;
		int    positionXTexte;
		int    positionYTexte;
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setStroke( new BasicStroke(3) );
		
		g2.setFont( new Font( "SansSerif", Font.BOLD, 14 ) );
		
		//Permet d'eviter la forme en excalier des fleches
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		// Obtenir les métriques de la police actuelle
		this.fm = g.getFontMetrics();
		
		
		// Centrage du texte (nom de la tâche)
		nom             = this.tache.getNom();
		largeurNom      = this.fm.stringWidth( nom );
		positionXTexte  = this.coordX + ( this.largeur / 2 ) - ( largeurNom          / 2 );
		positionYTexte  = this.coordY + ( this.hauteur / 4 ) + ( this.fm.getAscent() / 2 );
		
		g.drawString( nom, positionXTexte, positionYTexte );
		
		// Dessin du fond
		g.setColor( Color.WHITE );
		g.fillRect( this.coordX, this.coordY, this.largeur, this.hauteur );
		g.setColor( Color.BLACK );
		g.drawRect( this.coordX, this.coordY, this.largeur, this.hauteur );
		
		g.drawString( this.tache.getNom(), positionXTexte, positionYTexte );
		
		// Dessin de la ligne de séparation horizontale
		this.positionYLigne = this.coordY + this.hauteur / 2;
		g.drawLine( this.coordX, this.positionYLigne, this.coordX + this.largeur, this.positionYLigne );
		
		// Ligne verticale, séparant la partie basse en deux
		this.positionXLigne = this.coordX + this.largeur / 2;
		g.drawLine( this.positionXLigne, this.positionYLigne, this.positionXLigne, this.coordY + this.hauteur );
	}



	/**
	* Affiche la date au plus tôt dans la boîte de la tâche si elle est définie.
	* 
	* Cette méthode affiche, dans la partie inférieure gauche de la boîte,
	* soit le nombre de jours au plus tôt (ex: 4), soit une date formatée
	* si l'affichage en mode calendrier est activé (non encore géré ici).
	*
	* Actuellement, elle n'affiche que si la date au plus tôt (dateTot) est différente de -1
	* et si le mode calendrier est désactivé.
	*
	* @param g L’objet Graphics utilisé pour dessiner.
	* @param calendrier Indique si l’on doit afficher les dates en format calendrier
	*                   (ex: "12/06/2025") ou simplement le nombre de jours (ex: "4").
	*/
	public void afficherDateTot( Graphics g, boolean calendrier )
	{
		int    largeurTexte;
		String dateTotStr;
		
		if ( this.tache.getDateTot() != -1 )
		{
			if ( ! calendrier )
			{
				dateTotStr = String.valueOf( this.tache.getDateTot() );
				largeurTexte = this.fm.stringWidth( dateTotStr );
				
				this.afficherTot( g, dateTotStr , largeurTexte, calendrier);
			}
		}
	}



	/**
	* Affiche la date au plus tard (en jours) dans la boîte, si elle est définie
	* et que le mode calendrier est désactivé.
	*
	* @param g L'objet Graphics pour le dessin.
	* @param calendrier true pour afficher en mode calendrier, false sinon.
	* @return La date au plus tard sous forme de chaîne, vide si non affichée.
	*/
	public String afficherDateTard( Graphics g, boolean calendrier )
	{
		int    largeurTexte;
		String dateTardStr;
		
		dateTardStr = "";
		
		if ( this.tache.getDateTard() != -1 )
		{	
			if ( ! calendrier )
			{
				dateTardStr = String.valueOf( this.tache.getDateTard() );
				largeurTexte = this.fm.stringWidth( dateTardStr );
					
					
				this.afficherTard( g, dateTardStr , largeurTexte, calendrier);
			}
		}
		
		return dateTardStr;
	}




	/**
	* Affiche la date calculée (tot ou tard) au format calendrier (JJ/MM/AA)
	* en ajoutant le nombre de jours à une date de départ manuelle.
	*
	* @param g          L'objet Graphics pour le dessin.
	* @param calendrier true si affichage en mode calendrier.
	* @param position   "tot" ou "tard" pour choisir quelle date afficher.
	*/
	public void afficherDateCalendier( Graphics g, boolean calendrier, String position )
	{
		int               largeurTexte;
		String            dateStr;
		GregorianCalendar cal;
		String            dateManuelle;
		String[]          partie;
		
		int jour, mois, annee;
		
		dateManuelle = this.frame.getPanelAction().getDateManuelle();
		
		partie = dateManuelle.split( "\\/" );
		
		jour  = Integer.parseInt( partie[0] );
		mois  = Integer.parseInt( partie[1] ) - 1 ;
		annee = Integer.parseInt( partie[2] );
		
		cal = new GregorianCalendar(annee, mois, jour);
		
		
		if( position.equals("tard"))
		{
			cal.add( cal.DAY_OF_MONTH, this.tache.getDateTard() );
		}
		else
		{
			cal.add( cal.DAY_OF_MONTH, this.tache.getDateTot() );
		}
		
		dateStr = String.format("%02d/%02d/%02d",
		              cal.get( cal.DAY_OF_MONTH ),
		              cal.get( cal.MONTH        ) +   1,  // Calendar.MONTH est basé à 0
		              cal.get( cal.YEAR         ) % 100 );
		
		largeurTexte = this.fm.stringWidth( dateStr );
		
		if( position.equals("tard"))
		{
			this.afficherTard( g, dateStr , largeurTexte, calendrier);
		}
		else
		{
			this.afficherTot( g, dateStr , largeurTexte, calendrier);
		}
	}



	/**
	* Affiche la date au plus tard dans la partie inférieure droite de la boîte de tâche.
	*
	* @param g             L'objet Graphics pour le dessin.
	* @param dateTardStr   La date à afficher (sous forme de chaîne).
	* @param largeurTexte  La largeur en pixels du texte.
	* @param calendrier    true si l'affichage est en mode calendrier, false sinon.
	*/
	public void afficherTard(Graphics g, String dateTardStr , int largeurTexte, boolean calendrier)
	{
		int    xCentre, yTexte;
		
		xCentre = this.coordX + this.largeur / 2 + ( this.largeur / 4 ) - ( largeurTexte / 2 );
		yTexte  = this.positionYLigne + this.hauteur / 4 + this.hauteur / ( this.hauteur / 5 );
		
		if (!calendrier) {this.dateTard = Integer.parseInt( dateTardStr );}
		g.setColor( new Color( 0xD41111 ) );
		g.drawString( dateTardStr, xCentre, yTexte );
	}


	/**
	* Affiche la date au plus tôt dans la partie inférieure gauche de la boîte de tâche.
	*
	* @param g             L'objet Graphics pour dessiner.
	* @param dateTotStr    La date à afficher (sous forme de chaîne).
	* @param largeurTexte  La largeur du texte en pixels.
	* @param calendrier    true si en mode calendrier, false sinon (met à jour la valeur interne).
	*/
	public void afficherTot(Graphics g, String dateTotStr , int largeurTexte, boolean calendrier)
	{
		int    xCentre, yTexte;
		
		// Centré dans la moitié gauche
		xCentre = this.coordX + ( this.largeur / 4 ) - ( largeurTexte / 2 );
		yTexte  = this.positionYLigne + this.hauteur / 4 + this.hauteur / ( this.hauteur / 5 );
		
		if (!calendrier) {this.dateTot = Integer.parseInt( dateTotStr );}
		g.setColor( new Color( 0x44AD33 ) );
		g.drawString( dateTotStr, xCentre, yTexte );
	}
}
