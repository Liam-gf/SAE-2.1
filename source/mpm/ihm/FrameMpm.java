/**
* Classe FrameMpm : Permet d'afficher nos 2 panel.
* Groupe : 5
* @author :  Bastien Cantoni, Liam Girard--Fourneaux, Floriane Lepiller, Hugo Varao Gomes Da Silva
* Date : 13/06/2025
*/

package mpm.ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import mpm.Controleur;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class FrameMpm extends JFrame
{
	private Controleur  ctrl;
	
	private PanelMpm    panelMpm;
	private PanelAction panelAction;
	
	private JScrollPane scrollPane;
	
	/**
	* Constructeur de la classe FrameMpm.
	* Initialise la fenêtre principale avec ses composants :
	* Un panneau central défilable (scrollPane) contenant le panelMpm, et un panneau latéral panelAction avec les boutons/actions.
	* Configure aussi la barre de menu fournie par panelAction.
	* 
	* @param ctrl le contrôleur principal de l'application, utilisé pour la gestion des données et événements
	*/
	public FrameMpm( Controleur ctrl )
	{
		this.ctrl = ctrl;
		
		this.setTitle ( "Méthode des Potentiels et antécédents Métra" );
		this.setLayout( new BorderLayout() );
		
		/* ------------------------------ */
		/* Création des composants        *
		/* ------------------------------ */
		
		this.panelMpm    = new PanelMpm( ctrl , this );
		
		this.scrollPane  = new JScrollPane(this.panelMpm);
		this.scrollPane.getHorizontalScrollBar().setUnitIncrement(50); // vitesse de scroll
		
		
		this.panelAction = new PanelAction( ctrl );
		this.panelAction.setPreferredSize( new Dimension( this.ctrl.getLargeurFenetre() / 4, 0 ) );
		
		/* ------------------------------ */
		/* Positionnement des composants  */
		/* ------------------------------ */
		
		this.add( this.scrollPane ,    BorderLayout.CENTER );
		this.add( this.panelAction,    BorderLayout.EAST   );
		
		/* ------------------------------ */
		/* Activation des composants      */
		/* ------------------------------ */
		
		// Appel JMenuBar fournie par PanelAction une fois que tous les composants de la Frame sont initialisés.
		this.setJMenuBar(this.panelAction.construireMenuBar());
		
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible( true );
	}


	//Getteurs pour récupérer les 2 panels
	public PanelMpm    getPanelMpm()    { return this.panelMpm   ; }
	public PanelAction getPanelAction() { return this.panelAction; }


	/**
	 * Méthode maj() :
	 * Permet de réinitialiser proprement l'affichage de la fenêtre en remplaçant
	 * les deux panneaux (panelMpm et panelAction) par de nouvelles instances,
	 */
	public void maj()
	{
		PanelMpm    nouveauPanelMpm;
		PanelAction nouveauPanelAction;
		// Supprimer les anciens composants AVANT de recréer les nouveaux
		this.remove(panelAction); // important avant de créer le nouveau
		this.scrollPane.setViewportView(null); // vide le scrollPane
		
		// Recréer les nouveaux panels
		
		nouveauPanelMpm    = new PanelMpm   ( ctrl, this);
		nouveauPanelAction = new PanelAction( ctrl );
		
		// Mettre à jour le JScrollPane avec le nouveau panel MPM
		this.scrollPane.setViewportView(nouveauPanelMpm);
		
		// Ajouter le nouveau panelAction
		this.add(nouveauPanelAction, BorderLayout.EAST);
		
		// Reconfigurer la JMenuBar proprement
		this.setJMenuBar(nouveauPanelAction.construireMenuBar());
		
		// Mise à jour des références internes
		this.panelMpm    = nouveauPanelMpm;
		this.panelAction = nouveauPanelAction;
		
		// Forcer la mise à jour de la frame entière
		this.revalidate();
		this.repaint();
	}
}