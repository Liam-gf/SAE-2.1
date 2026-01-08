/**
* Classe Controleur : Permet de faire le lien entre l'ihm et le métier
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm;

import java.awt.Dimension;
import java.awt.Toolkit;
import mpm.ihm.FrameMpm;
import mpm.metier.Mpm;

public class Controleur
{
	private FrameMpm  frameMpm;
	private Mpm       mpm;
	
	private String    cheminFichier;
	private boolean   nouveauGraphe;
	private boolean   affichageTacheDynamique;
	
	private final int largFenetre;
	private final int hautFenetre;
	
	/**
	 * Constructeur du contrôleur principal.
	 * 
	 * Initialise la taille de la fenêtre selon la taille de l'écran,
	 * définit le fichier de données par défaut, lance l'initialisation du MPM,
	 * puis crée et configure la fenêtre principale.
	 */
	public Controleur()
	{
		Dimension tailleEcran;
		
		tailleEcran        = Toolkit.getDefaultToolkit().getScreenSize();
		this.affichageTacheDynamique = false;
		
		this.largFenetre   = (int) tailleEcran.getWidth ();
		this.hautFenetre   = (int) tailleEcran.getHeight();
		
		this.cheminFichier = "../exemples/vide.data";
		
		ctrlInitMpm( this.cheminFichier );
		
		this.frameMpm = new FrameMpm( this );
		this.frameMpm.setSize( this.largFenetre, this.hautFenetre );
	}


	public void setCheminFichier( String  chemin ) { this.cheminFichier           = chemin; }
	public void setNouveauGraphe( boolean valeur ) { this.nouveauGraphe           = valeur; }
	public void setAfTaDynamique( boolean b      ) { this.affichageTacheDynamique = b;      }


	public Mpm      getMpm()              { return this.mpm;                     }
	public FrameMpm getFrameMpm()         { return this.frameMpm;                }
	public int      getLargeurFenetre()   { return this.largFenetre;             }
	public int      getHautFenetre()      { return this.hautFenetre;             }
	public String   getCheminFichier()    { return this.cheminFichier;           }
	public boolean  getAfTaDynamique()    { return this.affichageTacheDynamique; }


	/**
	* Initialise l'objet Mpm à partir d'un fichier de données donné,
	* charge les données dans l'objet métier, et met à jour l'affichage
	* si la fenêtre FrameMpm est déjà créée.
	* 
	* @param chemin Le chemin du fichier de données à charger pour initialiser le Mpm.
	*/
	public void ctrlInitMpm( String chemin )
	{
		System.out.println( "Chargement de : " + chemin );  // debeugage trace utile
		
		this.mpm = new Mpm( this );
		
		this.cheminFichier = chemin;
		this.mpm.initMpm( chemin );
		
		this.setNouveauGraphe( true );
		
		if ( this.frameMpm != null )
		{
			this.frameMpm.maj();
		}
	}


	/**
	 * Change le type d'affichage des dates dans le panneau MPM.
	 * 
	 * Délègue la modification au panel via la méthode setTypeAffichageDate.
	 *
	 * @param type Le type d'affichage souhaité (ex : "tot", "lesDeux", "entier", "calendrier").
	 */
	public void changerTypeAffichageDate( String type )
	{
		this.frameMpm.getPanelMpm().setTypeAffichageDate( type );
	}


	/**
	 * Calcule et applique le chemin critique au panneau MPM.
	 * 
	 * Met à jour le chemin critique dans le panneau et déclenche son rafraîchissement.
	 */
	public void appliquerCheminCritique()
	{
		this.mpm.trouverCheminCritique();
		this.frameMpm.getPanelMpm().setCheminCritique( this.mpm.getCheminCritique() );
		this.frameMpm.getPanelMpm().repaint();
	}


	/**
	 * Rafraîchit (repaint) le panneau MPM si celui-ci est instancié.
	 */
	public void repaintPanelMpm()
	{
		if ( this.frameMpm != null && this.frameMpm.getPanelMpm() != null )
		{
			this.frameMpm.getPanelMpm().repaint();
		}
	}


	//Indique si un nouveau graphe a été créé. 
	public boolean estNouveauGraphe()   { return this.nouveauGraphe; }

	/**
	 * Point d'entrée de l'application.
	 * Crée une nouvelle instance du contrôleur.
	 */
	public static void main(String[] args)
	{
		new Controleur();
	}
}
