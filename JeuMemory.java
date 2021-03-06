import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Classe JeuMemory. Classe principale du jeu.
 * @author Alex Marcotte
 * @since Mars 2015
 **/

class JeuMemory {

	/**
	 * Methode main - l'entré du programme par la ligne de commande.
	 * @param args Paramètres entrés par ligne de commande
	 */
	public static void main(String[] args) {
		if (args.length < 5) {
			afficheAide();
			System.exit(0);
		}

		int rangees 		= Integer.parseInt(args[0]);
		int colonnes 		= Integer.parseInt(args[1]);
		int delai_initial 	= Integer.parseInt(args[2]);
		int delai_erreur 	= Integer.parseInt(args[3]);
		int num_theme 		= Integer.parseInt(args[4]);

		InterfaceJeu jeu = new InterfaceJeu(rangees, colonnes, delai_initial, delai_erreur, num_theme);
	}

	/**
	 * Méthode afficheAide
	 * Affiche l'aide lorsque les paramètres entrés par ligne de commande
	 * ne sont pas valide (si le nombre total de paramètre est inférieur
	 * au nombre requis)
	 */
	private static void afficheAide() {
		System.out.println("Utilisation: java JeuMemory <nRangées> <nColonnes> <délaiAffichageInitial(ms)> <delaiAffichageMauvaisePaire(ms)> <numeroDeTheme>");
		System.out.println("Ex: java JeuMemory 5 6 5000 1000 3");
		System.out.println("Voici la liste des thèmes disponibles:");
		System.out.println("0: Cartes couleurs");
		System.out.println("1: Lettres A...Z");
		System.out.println("2: Noms d'émotions");
		System.out.println("3: Images d'animaux");
		System.out.println("4: Images de galaxies");
		System.out.println("5: Mélange des thèmes 0 à 4");
	}

	/**
	 * Classe InterfaceJeu représente les éléments graphiques du jeu
	 * @see JeuMemory
	 */
	public static class InterfaceJeu {
		private GenerateurDeCartes generateur;
		private PanneauDeCartes panneau;
		private int rangees;
		private int colonnes;
		private int delai_initial;
		private int delai_erreur;
		private int num_theme;
		private JPanel inside;
		private JPanel boutons;
		private JFrame frame;
		private Carte[] cartes;

		// Les dictionnaires de cartes pour chacun des types
		private String[] lettres = "abcdefghijklmnopqrstuvwxyz".split("(?!^)");
		private String[] emotions = {"Joie", "Heureux", "Fier", "Passion", "Amour", "Outrage", "Furie", "Bougon", "Triste", "Chagrin", "Nerveux", "Souci", "Effroi", "Honte"};
		private String[] animaux = {"images/animaux/bird.jpg", "images/animaux/camel.jpg", "images/animaux/cheetah.jpg", "images/animaux/elephant.jpg", "images/animaux/gorille.jpg", "images/animaux/hare.jpg", "images/animaux/polarbear.jpg", "images/animaux/seal.jpg", "images/animaux/tiger.jpg"};
		private String[] galaxie = {"images/galaxie/galaxy1.jpg", "images/galaxie/galaxy2.jpg", "images/galaxie/galaxy3.jpg", "images/galaxie/galaxy4.jpg", "images/galaxie/galaxy5.jpg", "images/galaxie/galaxy6.jpg", "images/galaxie/galaxy7.jpg", "images/galaxie/galaxy8.jpg", "images/galaxie/galaxy9.jpg", "images/galaxie/galaxy10.jpg"};

		/**
		 * Constructeur pour InterfaceJeu
		 * @param rangees			Nombre de rangées pour la grille de cartes
		 * @param colonnes			Nombre de colonnes pour la grille
		 * @param delai_initial		Délai initial avant de retourner les cartes
		 * @param delai_erreur		Délai après une erreur avant de retourner les cartes
		 * @param num_theme			Numéro du thème
		 */
		public InterfaceJeu(int rangees, int colonnes, int delai_initial, int delai_erreur, int num_theme) {
			this.rangees = rangees;
			this.colonnes = colonnes;
			this.delai_initial = delai_initial;
			this.delai_erreur = delai_erreur;
			this.num_theme = num_theme;

			cartes = new Carte[rangees*colonnes];

			// Dépendemment du thème voulu...
			if (num_theme == 0) { // Couleur
				generateur = new GenerateurDeCartesCouleur("Couleurs");
			} else if (num_theme == 1) { // Lettres
				generateur = new GenerateurDeCartesMot("Lettres", lettres);
			} else if (num_theme == 2) { // Émotions
				generateur = new GenerateurDeCartesMot("Emotions", emotions);
			} else if (num_theme == 3) { // Image animaux
				generateur = new GenerateurDeCartesImage("Animaux", animaux);
			} else if (num_theme == 4) { // Image galaxie
				generateur = new GenerateurDeCartesImage("Galaxie", galaxie);
			} else if (num_theme == 5) {
				GenerateurDeCartes[] generateurs = {new GenerateurDeCartesCouleur("Couleurs"), new GenerateurDeCartesMot("Lettres", lettres),
					new GenerateurDeCartesMot("Emotions", emotions), new GenerateurDeCartesImage("Animaux", animaux), new GenerateurDeCartesImage("Galaxie", galaxie) };
				generateur = new GenerateurDeCartesMultiple("Multiple", generateurs);
			}
			
			cartes = generateur.generePairesDeCartesMelangees(rangees*colonnes/2);
			panneau = new PanneauDeCartes(rangees, colonnes, cartes, delai_initial, delai_erreur);

			// Le JFrame principal du jeu
			frame = new JFrame("JeuMemory");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(colonnes*200, rangees*200);

			// Un JPanel contiendra l'intérieur du jeu et un autre contient les boutons
	 		inside = new JPanel(new BorderLayout());
	 		boutons = new JPanel(new FlowLayout());

			// Bouton 'quitter' et son ActionListener
			JButton quitter = new JButton("Quitter");
			quitter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
				}
			});

			// Bouton 'recommencer' et son ActionListener
			JButton recommencer = new JButton("Recommencer");
			recommencer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String[] types = { "Couleur", "Lettres", "Emotions", "Animaux", "Galaxie", "Melange"};
    				String choix = (String) JOptionPane.showInputDialog(
    					null, "Choisissez un type de carte", "Nouvelle partie", JOptionPane.QUESTION_MESSAGE, null,
        				types, types[num_theme]);
    				
    				int theme;
    				switch (choix) {
    					case "Couleur":
    						theme = 0;
    						break;
    					case "Lettres":
    						theme = 1;
    						break;
    					case "Emotions":
    						theme = 2;
    						break;
    					case "Animaux":
    						theme = 3;
    						break;
    					case "Galaxie":
    						theme = 4;
    						break;
    					case "Melange":
    						theme = 5;
    						break;
    					default:
    						theme = 0;
    				}
    				frame.dispose();
    				// On garde les mêmes paramètres sauf le thème pour la nouvelle partie
    				new InterfaceJeu(rangees, colonnes, delai_initial, delai_erreur, theme);
				}
			});

			// Bouton 'resoudre' et son ActionListener
			JButton resoudre = new JButton("Resoudre");
			resoudre.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panneau.resoudreJeu();
				}
			});

			// On ajoute les boutons au JPanel qui les contiendra
			boutons.add(resoudre);
			boutons.add(recommencer);
			boutons.add(quitter);

			// Ajoute les components au JPanel inside (panneau de cartes + boutons)
			inside.add(panneau, BorderLayout.CENTER);
			inside.add(boutons, BorderLayout.SOUTH);

			// Puis on ajoute le inside au JFrame
			frame.add(inside);
			frame.setVisible(true);
		}
	}

}