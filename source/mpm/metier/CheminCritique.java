/**
* Classe CheminCritique : Permet de stocker les chemins différent chemin critique 
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.metier;

import java.util.ArrayList;

public class CheminCritique 
{
	private ArrayList<ArrayList<Tache>> chemin;
	
	/**
	* Constructeur de la classe CheminCritique.
	* 
	* @param chemin Liste de chemins critiques à stocker.
	*/
	public CheminCritique( ArrayList<ArrayList<Tache>> chemin )
	{
		if ( this.chemin != null )
			this.chemin.clear();
		
		this.chemin = chemin;
	}

	// methode pour avoir le ou les chemin critique
	public ArrayList<ArrayList<Tache>> getChemin()
	{
		return chemin;
	}
}