/**
 * Classe Tache : Permet de créer des tâches et de renseigner leurs informations.
 * Exercice 2.2 : Visualisation CUI.
 * Groupe : 5
 * @author : Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
 * Date : 02/06/2025
 */

package mpm.metier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Tache
{
	private String         nom;
	private int            duree;
	private int            dateTot;
	private int            dateTard;
	
	private Tache          dateTotPrcPlusGrand;
	private Tache          dateTardSvtPlusPetit;
	
	private ArrayList<Tache> lstTachePrc;
	private ArrayList<Tache> lstTacheSvt;

	
	/**
	 * Constructeur de la classe Tache.
	 * 
	 * Initialise une tâche avec un nom, une durée et une liste de tâches précédentes.
	 * Les dates (totale et tardive) ainsi que le niveau sont initialisés à -1 (non calculés).
	 * La liste des tâches suivantes est initialisée vide.
	 * 
	 * @param nom         Le nom de la tâche.
	 * @param duree       La durée de la tâche.
	 * @param lstTachePrc La liste des tâches précédentes (prérequis) de cette tâche.
	 */
	public Tache( String nom, int duree, ArrayList<Tache> lstTachePrc )
	{
		this.nom         = nom;
		this.duree       = duree;
		
		this.dateTot     = -1;
		this.dateTard    = -1;
		
		this.lstTachePrc = lstTachePrc;
		this.lstTacheSvt = new ArrayList<Tache>();
	}


	public void setNom     ( String           nom   ) { this.nom         = nom;   }
	public void setDuree   ( int              duree ) { this.duree       = duree; }
	public void setTachePrc( ArrayList<Tache> t     ) { this.lstTachePrc = t;     }
	public void setTacheSvt( ArrayList<Tache> t     ) { this.lstTacheSvt = t;     }


	public String            getNom()                   { return this.nom;                  }
	public int               getDuree()                 { return this.duree;                }
	public int               getDateTot()               { return this.dateTot;              }
	public int               getDateTard()              { return this.dateTard;             }
	public ArrayList<Tache>  getPrc()                   { return this.lstTachePrc;          }
	public ArrayList<Tache>  getSvt()                   { return this.lstTacheSvt;          }
	public Tache             getTachePrcMaxDateTot()    { return this.dateTotPrcPlusGrand;  }
	public Tache             getTacheTardSvtPlusPetit() { return this.dateTardSvtPlusPetit; }


	
	/**
	 * Calcule la date totale au plus tôt (dateTot) pour la tâche.
	 * 
	 * Si la tâche a des précédents, dateTot est la fin la plus tardive
	 * parmi eux (dateTot + durée). Sinon, dateTot = 0.
	 * 
	 * Si aucun précédent n’a de dateTot valide, dateTot reste à -1.
	 * Met à jour aussi la tâche précédente avec la plus grande dateTot.
 	*/
	public void calculerDateTot()
	{
		boolean calculPossible;
		int max;
		int calcule;

		if ( this.aPrecedent() )
		{
			max = -1;
			calculPossible = false;
			
			for ( Tache tache : this.lstTachePrc )
			{
				if ( tache.getDateTot() != -1 )
				{
					this.dateTotPrcPlusGrand = tache;
					calcule = tache.getDateTot() + tache.getDuree();
					
					if ( calcule > max )
					{
						max = calcule;
						
						//utile plus tard pour le PopUp
						this.dateTotPrcPlusGrand =  tache;
					}
					
					calculPossible = true;
				}
			}
			
			if ( calculPossible )
			{
				this.dateTot = max;
			}
			else
			{
				this.dateTot = -1;
			}
		}
		else
		{
			this.dateTot = 0;
		}
	}


	/**
	 * Calcule la date tardive (dateTard) de la tâche.
	 * 
	 * Si la tâche a des successeurs, dateTard est la plus petite dateTard
	 * parmi eux, moins la durée de cette tâche.
	 * Sinon (tâche terminale), dateTard = dateTot.
	 * 
	 * Met à jour aussi la tâche suivante avec la plus petite dateTard.
	 */
	public void calculerDateTard()
	{
		int minDateTotSvt;
		
		// Si la tâche a des tâches suivantes (c’est-à-dire si ce n’est pas une tâche terminale) :
		if ( this.aSuivant() )
		{
			minDateTotSvt = this.lstTacheSvt.get(0).getDateTard();
			this.dateTardSvtPlusPetit = this.lstTacheSvt.get(0);
			
			for ( Tache svt : this.lstTacheSvt )
			{
				if ( svt.getDateTard() < minDateTotSvt )
				{
					this.dateTardSvtPlusPetit = svt;
					minDateTotSvt = svt.getDateTard();
				}
			}
			
			this.dateTard = minDateTotSvt - this.getDuree();
		}
		else
		{
			this.dateTard = this.dateTot;
			this.dateTardSvtPlusPetit = null;
		}
	}

	/**
	 * Ajoute une tâche suivante si elle n'est pas déjà dans la liste.
	 * 
	 * @param tacheSuivante La tâche à ajouter comme successeur.
	 */
	public void ajouterTacheSvt( Tache tacheSuivante )
	{
		if ( ! this.lstTacheSvt.contains( tacheSuivante ) )
		{
			this.lstTacheSvt.add( tacheSuivante );
		}
	}

	/**
	 * Ajoute une tâche précédente si elle n'est pas déjà dans la liste.
	 * 
	 * @param tachePrc La tâche à ajouter comme précédant.
	 */
	public void ajouterTachePrc( Tache tachePrc )
	{
		if ( ! this.lstTachePrc.contains( tachePrc ) )
		{
			this.lstTachePrc.add( tachePrc );
		}
	}

	//methodes utilitaires qui servent a savoir si une tache a des suivants 
	//ou des précédents ou non
	public boolean aSuivant()   { return ! this.getSvt().isEmpty(); }
	public boolean aPrecedent() { return ! this.getPrc().isEmpty(); }


	/**
	 * Retourne une représentation textuelle détaillée de la tâche,
	 * incluant son nom, sa durée, ses dates, ainsi que la liste des tâches
	 * précédentes et suivantes.
	 *
	 * @return Une chaîne décrivant la tâche et ses relations.
	 */
	public String toString()
	{
		
		int    numMois;
		int    numJour;
		int    cpt;
		
		String sRet;
		
		GregorianCalendar cal;
		
		
		cal     = new GregorianCalendar();
		numMois = cal.get(Calendar.MONTH) + 1;
		numJour = cal.get(Calendar.DAY_OF_MONTH);
		
		sRet = "";
		
		sRet += this.getNom() + " : " + this.getDuree() + " jour";
		
		if ( this.getDuree() > 1 ) { sRet += "s"; }
		
		sRet += "\n";
		
		cal.add( cal.DAY_OF_MONTH, this.dateTot );

		sRet += String.format("%02d/%02d" ,
		        cal.get(cal.DAY_OF_MONTH),
		        cal.get(cal.MONTH) + 1  );
		
		
		if ( this.getDateTard() - this.getDateTot() > 1 ) { sRet += "s"; }
		
		sRet += "\n";
		
		
		if ( this.lstTachePrc.isEmpty() )
		{
			sRet += "   pas de tâche précédente\n";
		}
		else
		{
			sRet += "   liste des tâches précédentes :\n      ";
			
			cpt = 1;
			
			for ( Tache tPrc : this.lstTachePrc )
			{
				sRet += tPrc.getNom();
				
				if ( lstTachePrc.size() > cpt )
				{
					sRet += ", ";
					cpt ++;
				}
			}
			
			sRet += "\n";
		}
		
		
		if ( this.lstTacheSvt.isEmpty() )
		{
			sRet += "   pas de tâche suivante\n";
		}
		else
		{
			sRet += "   liste des tâches suivantes :\n      ";
			
			cpt = 1;
			
			for ( Tache tSvt : this.lstTacheSvt )
			{
				sRet += tSvt.getNom();
				
				if ( lstTacheSvt.size() > cpt )
				{
					sRet += ", ";
					cpt ++;
				}
			}
			
			sRet += "\n";
		}
		
		return sRet;
	}
}
