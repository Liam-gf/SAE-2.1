/**
* Classe Mpm : Permet de gérer notre graphe mpm de manière non graphique
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.metier;

import java.io.FileReader;
import java.lang.ModuleLayer.Controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mpm.Controleur;

public class Mpm
{
	private Controleur                  ctrl;
	private CheminCritique              cheminCritique;
	private List<Tache>                 listeTache;
	private ArrayList<ArrayList<Tache>> tabNiveau;
	private List<Tache>                 lstTacheSansDF;
	
	
	/**
	 * Constructeur de la classe Mpm.
	 * Initialise les structures de données nécessaires au calcul du chemin critique,
	 * ainsi que les listes de tâches et les niveaux associés.
	 *
	 * @param ctrl Le contrôleur principal de l'application, permettant l'accès aux données et à l'interface.
	 */
	public Mpm( Controleur ctrl )
	{
		ArrayList<ArrayList<Tache>> chemin;
		
		this.ctrl = ctrl;
		
		chemin = new ArrayList<ArrayList<Tache>>();
		
		this.cheminCritique = new CheminCritique( chemin );
		this.listeTache     = new ArrayList<Tache>();
		this.tabNiveau      = new ArrayList<ArrayList<Tache>>();
	}


	public CheminCritique              getCheminCritique()   { return this.cheminCritique;   }
	public List<Tache>                 getListeTache()       { return this.listeTache;       }
	public ArrayList<ArrayList<Tache>> getTabNiveau()        { return this.tabNiveau;        }
	public List<Tache>                 getListeTacheSansDF() { return this.lstTacheSansDF;   }
	public List<Tache>                 getTaches()           { return this.listeTache;       }
	public int                         getNombreNiveau()     { return this.tabNiveau.size(); }


	//méthode utilitaire qui permet de chercher une tache grace a son nom
	public Tache getTacheParNom( String nom )
	{
		for ( Tache t : this.listeTache )
		{
			if ( t.getNom().equalsIgnoreCase( nom.trim() ) )
			{
				return t;
			}
	}
	
	return null;
	}

	//méthode utilitaire qui permet de recupérer le maximum de tache dans un niveau
	public int getMaxNiveau()
	{
		int max;
		
		max = -1;
		
		for ( ArrayList<Tache> listeNiveau : this.tabNiveau )
		{
			if ( listeNiveau.size() > max )
			{
				max = listeNiveau.size();
			}
		}
		
		return max;
	}

	/**
	 * Initialise les données du MPM à partir d'un fichier.data.
	 * 
	 * Cette méthode :
	 *
	 * - Lit un fichier de données texte, crée les tâches avec leurs durées et leurs précédents à partir du fichier.
	 * - Si le mode dynamique est activé, récupère directement la liste des tâches dynamiques.
	 * - Ajoute automatiquement les tâches de début et de fin.
	 * - Calcule les niveaux des tâches, leurs dates, et met à jour l'affichage.
	 *
	 * @param data Le chemin vers le fichier.data.
	 */
	public void initMpm( String data )
	{
		int       duree;
		String    ligne;
		String    nomTache;
		
		Scanner   scTache;
		String[]  partie;

		ArrayList<String[]> tabPrc;
		ArrayList<ArrayList<String[]>> listeTemp;
		
		Tache tacheCourante ;
		
		// Nettoyage des anciennes données
		this.listeTache.clear();
		this.tabNiveau .clear();
		
		this.cheminCritique = new CheminCritique( new ArrayList<ArrayList<Tache>>() );
		
		listeTemp = new ArrayList<ArrayList<String[]>>();
		
		if ( this.ctrl.getAfTaDynamique() == false )
		{
			// scanner pour lire un fichier .data
			try
			{
				scTache = new Scanner( new FileReader( data ) );
				
				while ( scTache.hasNextLine() )
				{
					ligne = scTache.nextLine();
					
					// On ignore les lignes vides et les tache de début et fin
					if ( ligne.equals( "" ) || ligne.startsWith( "Début" ) || ligne.startsWith( "Fin" ) ) continue;
					
					partie = ligne.split( "\\|" ); // on découpe le texte selon les "|"
					
					nomTache = partie[0];
					duree    = Integer.parseInt( partie[1] );
					
					// On ajoute les taches sans précédants
					this.listeTache.add( new Tache ( nomTache, duree, new ArrayList<Tache>() ) );
					
					tabPrc = new ArrayList<String[]>();
					
					if ( partie.length > 2 )
					{
						tabPrc.add( partie[2].split( "," ) ); // on découpe la partie des précedents selon les ","
					}
					else
					{
						tabPrc.add( new String[0] );
					}
					
					listeTemp.add( tabPrc );
				}
				
				scTache.close();
			}
				catch ( Exception e ) { e.printStackTrace();
			}
			
			for ( int cpt = 0; cpt < this.listeTache.size(); cpt++ )
			{
				tacheCourante = this.listeTache.get(cpt);
				tabPrc = listeTemp.get(cpt);
				
				for ( String[] nomsPrec : tabPrc )
				{
					for ( String nomPrec : nomsPrec )
					{
						for ( Tache t : this.listeTache )
						{
							if ( t.getNom().equals( nomPrec ) )
							{
								tacheCourante.ajouterTachePrc( t );
								t.ajouterTacheSvt( tacheCourante );
							}
						}
					}
				}
			}
		}
		else
		{
			this.listeTache = ctrl.getFrameMpm().getPanelAction().getListTacheDyn();
			this.ctrl.setAfTaDynamique( false );
		}
		
		this.initTacheFinEtDebut();//on ajoute les taches de Début et de fin
		
		this.creerNiveau();
		this.calculerDate();
		this.afficherTaches();



	}
	

	/**
	 * Initialise les tâches spéciales "Début" et "Fin" dans la liste des tâches.
	 * 
	 * - Ajoute une tâche "Début" et "Fin" en première position et dernière position.
	 * - Relie toutes les tâches sans précédents à la tâche "Début".
	 * - Relie toutes les tâches sans suivants à la tâche "Fin".
	 */
	public void initTacheFinEtDebut()
	{
		ArrayList<Tache> debut;
		
		// Initialisation : ajout de la tâche "Début"
		this.listeTache.add(0, new Tache ( "Début", 0, new ArrayList<Tache>() ) ); // ajout de la tache Début en position 0
		
		
		if ( listeTache.size() > 2 )
		{
			listeTache.add( new Tache ( "Fin", 0, new ArrayList<Tache>() ) );
		}
		else
		{
			debut = new ArrayList<Tache>();
			listeTache.add( new Tache ( "Fin", 0, debut ) );
		}
		
		for( Tache tache : this.listeTache )
		{
			if ( tache.getPrc().isEmpty() && ! tache.getNom().equals( "Début" ) )
			{
				this.listeTache.get(0).ajouterTacheSvt( tache );
				tache.ajouterTachePrc( this.listeTache.get(0) );
			}
			
			
			if ( tache.getSvt().isEmpty() && ! tache.getNom().equals( "Fin" ) && ! tache.getNom().equals( "Début" ) )
			{
				this.listeTache.get( listeTache.size() - 1 ).ajouterTachePrc( tache );
				tache.ajouterTacheSvt( this.listeTache.get( listeTache.size() - 1 ) );
			}
		}
	}



	/**
	 * Calcule les dates au plus tôt et au plus tard pour chaque tâche,
	 * en parcourant les niveaux dans l'ordre croissant puis décroissant.
	 */
	public void calculerDate()
	{
		// Calcul des dates totales (ordre croissant)
		for ( ArrayList<Tache> l : this.tabNiveau )
		{
			for ( Tache t : l )
			{
				t.calculerDateTot();
			}
		}
		
		// Calcul des dates tardives (ordre décroissant)
		for ( int i = this.tabNiveau.size() - 1; i >= 0; i-- )
		{
			for ( Tache t : this.tabNiveau.get(i) )
			{
				t.calculerDateTard();
			}
		}
	}

	/**
	 * Affiche toutes les tâches regroupées par niveau dans la console.
	 */
	public void afficherTaches()
	{
		//boucles pour l'affichage final
		for( ArrayList<Tache> tabTache : this.tabNiveau )
		{
			for( Tache tache : tabTache )
			{
				System.out.println( tache );
			}
		}
	}

	/**
	 * Crée les niveaux de tâches en fonction de leurs précédents.
	 * 
	 * Le premier niveau contient les tâches sans précédents.
	 * Les niveaux suivants sont construits itérativement en plaçant
	 * les tâches dont tous les précédents sont déjà placés.
	 */
	public void creerNiveau()
	{
		ArrayList<Tache> tachesPlacees;
		ArrayList<Tache> premierNiveau;
		ArrayList<Tache> niveauSuivant;
		boolean encore;
		boolean tousPrecedentsPlaces;
		
		tachesPlacees = new ArrayList<Tache>(); // Tâches déjà placées
		this.tabNiveau.clear(); // On vide les niveaux avant de commencer pour etre sur qui ya rien
		
		// Ajouter le premier niveau qui non pas de précédent
		premierNiveau = new ArrayList<Tache>();
		for ( Tache tache : this.listeTache )
		{
			if ( ! tache.aPrecedent() ) // pas de tâche précédente
			{
				premierNiveau.add( tache );
				tachesPlacees.add( tache );
			}
		}
		
		if ( ! premierNiveau.isEmpty() )
		{
			this.tabNiveau.add( premierNiveau );
		}
		
		encore = true;
		
		while ( encore )
		{
			niveauSuivant = new ArrayList<Tache>();
			
			for ( Tache tache : this.listeTache )
			{
				// On ne traite que les tâches non placées
				if ( ! tachesPlacees.contains( tache ) )
				{
					tousPrecedentsPlaces = true;
					
					// Vérifie si toute tâches précédentes sont déjà placées
					for ( Tache prec : tache.getPrc() )
					{
						if ( ! tachesPlacees.contains( prec ) )
						{
							tousPrecedentsPlaces = false;
							break;
						}
					}
					
					if ( tousPrecedentsPlaces )
					{
						niveauSuivant.add( tache );
					}
				}
			}
			
			if ( ! niveauSuivant.isEmpty() )
			{
				this.tabNiveau.add( niveauSuivant );
				tachesPlacees.addAll( niveauSuivant );
			}
			else
			{
				encore = false; // plus de tâches à placer, on arrête
			}
		}
	}

	/**
	 * Recherche et construit le(s) chemin(s) critique(s) dans le graphe des tâches.
	 * 
	 * Le chemin critique est constitué des tâches dont les dates totales et tardives sont égales,
	 * indiquant qu'elles ne peuvent être retardées sans retarder le projet.
	 * 
	 * Étapes :
	 * 1. Identifier les tâches de fin.
	 * 2. Sélectionner les tâches de fin critiques et initialiser les chemins avec ces tâches.
	 * 3. Remonter récursivement les précédents critiques pour construire tous les chemins critiques.
	 * 4. Gérer les bifurcations en clonant les chemins quand plusieurs précédents critiques existent.
	 * 5. Stocker les chemins critiques trouvés dans l'objet CheminCritique.
	 */
	public void trouverCheminCritique()
	{
		ArrayList<ArrayList<Tache>> chCritique;
		ArrayList<ArrayList<Tache>> nouveauxChemins;
		ArrayList<ArrayList<Tache>> contienTousLesChemins;
		
		ArrayList<Tache> newChemin;
		ArrayList<Tache> tacheFin ;
		ArrayList<Tache> chemin;
		ArrayList<Tache> precedents;
		ArrayList<Tache> ajoutTacheTemp ;
		
		Tache derniere;
		
		chCritique = new ArrayList<ArrayList<Tache>>();
		tacheFin = new ArrayList<Tache>();
		
		contienTousLesChemins = new ArrayList<ArrayList<Tache>>();
		
		// Étape 1 : trouver les tâches de fin
		tacheFin = this.tabNiveau.get( this.tabNiveau.size() -1 );
		
		// Étape 2 :je  sélectionne les tâches de fin critiques et créer des chemins initiaux
		for ( Tache t :tacheFin )
		{
			//A cette etape on vérifie si dans les tache de fin ya pas une ou des tache critique
			if ( t.getDateTot() == t.getDateTard() )
			{
				newChemin = new ArrayList<Tache>();
				newChemin.add( t );
				contienTousLesChemins.add( newChemin );
			}
		}
		
		// Étape 3 : remonter les précédents critiques
		boolean encore = true;
		
		while ( encore )
		{
			encore = false;
			
			// On va travailler sur une copie temporaire pour éviter ConcurrentModificationException
			nouveauxChemins = new ArrayList<ArrayList<Tache>>();
			
			//Maintenant je je regarde ma liste contienTousLesChemins qui contient tous les chemin critique. 
			for ( int ind = 0; ind < contienTousLesChemins.size(); ind++ )
			{
				chemin = contienTousLesChemins.get( ind );
				derniere = chemin.get( chemin.size() - 1 );
				
				//Attention je pointe juste vers la list deja existante
				precedents = derniere.getPrc();
				
				// Liste des précédents critiques
				ajoutTacheTemp = new ArrayList<Tache>();
				
				for ( Tache prec : precedents )
				{
					if ( prec.getDateTot() == prec.getDateTard() )
					{
						ajoutTacheTemp.add( prec );
					}
				}
				
				if ( ajoutTacheTemp.size() > 1 )
				{
					// Pour chaque précédent critique sauf le premier, on clone le chemin et on ajoute le précédent
					for ( int i = 1; i < ajoutTacheTemp.size(); i++ )
					{
						newChemin = new ArrayList<Tache>( chemin );
						newChemin.add( ajoutTacheTemp.get( i ) );
						nouveauxChemins.add( newChemin );
					}
					
					// Au chemin original, on ajoute le premier précédent critique
					chemin.add( ajoutTacheTemp.get(0) );
					encore = true;
				}
				else
				{
					if ( ajoutTacheTemp.size() == 1 )
					{
						// Si un seul précédent critique, on l'ajoute au chemin
						chemin.add(ajoutTacheTemp.get(0));
						encore = true;
					}
				}
				// Sinon, pas de précédent critique, chemin terminé
			}
			
			//Fin du grand fort. On ajoute les nouveaux chemins créés dans la liste principale
			contienTousLesChemins.addAll( nouveauxChemins );
		}
		
		// Étape 5 : Stocker dans la class CheminCritique le /les chemin(s) trouver
		this.cheminCritique = new CheminCritique( contienTousLesChemins );
	}

	//methode utilitaire permettant d'ajouter une tache dans la listeTache en la plaçant avant la tache "Fin"
	public void ajouterTache( Tache t )
	{
		this.listeTache.add( this.listeTache.size() - 1, t );
	}
}
