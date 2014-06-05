package com.python4d.azgame;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class AZgameActivity extends Activity {

	SharedPreferences.Editor editor = null;
	SharedPreferences preferences = null;
	private String HSLocalName;
	private int HSLocal, HSLocalLevel;
	private String HSWebName;
	private int HSWeb, HSWebLevel;

	FTPConnect ftp = null;

	private AZgameActivity ma;
	static boolean pub = true;
	boolean bGameOver=false;
	private int SWIPE_MIN_DISTANCE = 100;
	// private static final int SWIPE_MAX_OFF_PATH = 500;
	private final int SWIPE_THRESHOLD_VELOCITY = 50;
	private TextView textScore, textLevel, textHSWeb, textHSLocal;
	private Button[][] buttonsAZgame;
	private GestureDetectorCompat mDetector;
	private Integer[][] buttonVal;
	private int blockActif = 1;
	private int block = 0, place = 0;

	private int indice_vide = 8;
	private int score, level;
	private Drawable defaultButtonDrawable;

	static String DEBUG_TAG = "AZgame";
	protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 1;
	protected static final int SERVICE_MISSING = 1;

	private enum Direction {
		up, down, left, right
	};

	private enum ListeOfGame {
		FourSquare, OneSquare
	};

	private ListeOfGame gameselect = ListeOfGame.OneSquare;
	// MAX correspond à la lettre max (normalement Z=26+1) pour laquelle on
	// revient à la lettre A
	private int MAX = 27;
	private boolean bPause = false;
	private ImageView image;
	// DIFFICULTE
	// facile=7
	// moyen=8
	// difficile=9
	private static final int difficulte = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ma = this;
		switch (gameselect) {
		case FourSquare:
			setContentView(R.layout.four_square);
			textScore = (TextView) findViewById(R.id.textScore);
			textLevel = (TextView) findViewById(R.id.textLevel);
			score = 0;
			level = 1;
			textScore.setText("Score=" + score);
			textLevel.setText("Level=" + level);
			blockActif = 1;
			buttonsAZgame = new Button[4][9];
			buttonVal = new Integer[4][9];
			block = 0;
			place = 0;
			for (Button[] i : buttonsAZgame) {
				place = 0;
				int startWhere = new Random().nextInt(9);
				for (Button j : i) {
					String str = "Button" + (block + 1) + "" + (place + 1);
					try {
						j = (Button) findViewById((Integer) R.id.class
								.getField(str).get(new R()));
						j.setClickable(false);
						buttonVal[block][place] = (startWhere == place) ? 1 : 0;
						j.setText(""
								+ ((buttonVal[block][place] == 0) ? ""
										: (char) (buttonVal[block][place]
												.intValue() + 64)));
						buttonsAZgame[block][place] = j;
						place++;
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				block++;
			}
			defaultButtonDrawable = buttonsAZgame[0][0].getBackground();
			mDetector = new GestureDetectorCompat(this, new MyGestureListener());
			break;
		case OneSquare:
			Aide(null);
			setContentView(R.layout.one_square);

			// récupérer le highscore local du téléphone
			getLocalHighScore();
			// Lancer en Thread une lecture du highscore web et vérification par
			// rapport au score local
			verifyWebHighScore();

			// mettre le fond d'écran et l'animation
			image = (ImageView) findViewById(R.id.ImageDeFond);
			image.setImageResource(R.drawable.jimi_hendrix_450x563);
			Animation animation = AnimationUtils.loadAnimation(
					getApplicationContext(), R.anim.imagedefond);
			image.startAnimation(animation);

			NewGame();

			defaultButtonDrawable = buttonsAZgame[0][0].getBackground();
			mDetector = new GestureDetectorCompat(this, new MyGestureListener());
			try {
				if (pub) {
					AdView adView = (AdView) this.findViewById(R.id.adView);
					AdRequest adRequest = new AdRequest.Builder().build();
					adView.loadAd(adRequest);
				}
			} catch (Throwable e) {
				Log.e("Error: GooglePlayServiceUtil: ", "" + e);
			}
			break;
		}

	}

	private void NewGame() {
		// TODO Auto-generated method stub
		textScore = (TextView) findViewById(R.id.textScore);
		textLevel = (TextView) findViewById(R.id.textLevel);
		score = 0;
		level = 1;
		textScore.setText("Score=" + score);
		textLevel.setText("Level=" + level);
		buttonsAZgame = new Button[1][9];
		buttonVal = new Integer[1][9];
		block = 0;
		place = 0;
		indice_vide = 8;
		int startWhere = new Random().nextInt(9);
		for (Button j : buttonsAZgame[0]) {
			String str = "Button1" + (place + 1);
			try {
				j = (Button) findViewById((Integer) R.id.class.getField(str)
						.get(new R()));
				j.setClickable(false);
				buttonVal[block][place] = (startWhere == place) ? 1 : 0;
				j.setText(""
						+ ((buttonVal[0][place] == 0) ? ""
								: (char) (buttonVal[block][place].intValue() + 64)));
				buttonsAZgame[0][place] = j;
				place++;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void GameOver() {
		bGameOver=true;
		if (score > HSLocal) {
			// Préparer la boite d'Alert pour récupérer le nom du champion (il
			// suffit de faire .show() pour lancer la boite)
			AlertDialog.Builder Alert_getname = null;
			final EditText name_EditText = new EditText(this);
			name_EditText.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
					| InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			name_EditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							10) });
			Alert_getname = new AlertDialog.Builder(this)
					.setTitle("HighScore !")
					.setMessage(
							"Bravo!\n Nouveau record ! \nEntre tes initiales:")
					.setView(name_EditText)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = name_EditText.getText()
											.toString();
									HSLocalName = value;
									HSLocal = score;
									HSLocalLevel = level + 1;
									setLocalHighScore();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									assert true;
								}
							});
			Alert_getname.show();
		}
		verifyWebHighScore();
		String GameOverStr = "GAME-OVER";
		for (int i = 0; i < 9; i++) {
			buttonsAZgame[0][i].setTypeface(null, Typeface.BOLD);
			buttonsAZgame[0][i].setTextColor(getResources().getColor(
					R.color.Red));
			buttonsAZgame[0][i].setText(GameOverStr.substring(i, i + 1));
		}
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";

		@Override
		public boolean onDown(MotionEvent event) {
			Log.d(DEBUG_TAG, "onDown: " + event.toString());
			if (bGameOver){
				NewGame();
				bGameOver=false;
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			// Log.d(DEBUG_TAG,"onFling: " + event1.toString() +
			// event2.toString());
			try {
				// if (Math.abs(event1.getY() - event2.getY()) >
				// SWIPE_MAX_OFF_PATH) {
				// return false;
				// }
				// diagonal vers gauche-haut
				if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
						&& event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
						&& (Math.abs(velocityX) + Math.abs(velocityY)) > SWIPE_THRESHOLD_VELOCITY
						&& gameselect == ListeOfGame.FourSquare) {
					onLeftUpSwipe();
				}
				// diagonal vers droite-bas
				else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
						&& event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
						&& (Math.abs(velocityX) + Math.abs(velocityY)) > SWIPE_THRESHOLD_VELOCITY
						&& gameselect == ListeOfGame.FourSquare) {
					onRightDownSwipe();
				}
				// diagonal vers gauche-bas
				else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
						&& event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
						&& (Math.abs(velocityX) + Math.abs(velocityY)) > SWIPE_THRESHOLD_VELOCITY
						&& gameselect == ListeOfGame.FourSquare) {
					onLeftDownSwipe();
				}
				// diagonal vers droite-haut
				else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
						&& event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
						&& (Math.abs(velocityX) + Math.abs(velocityY)) > SWIPE_THRESHOLD_VELOCITY
						&& gameselect == ListeOfGame.FourSquare) {
					onRightUpSwipe();
				}
				// right to left swipe
				else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onLeftSwipe();
				}
				// left to right swipe
				else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					onRightSwipe();
				}
				// haut vers le bas
				else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					onDownSwipe();
				}
				// bas vers le haut
				else if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					onUpSwipe();
				}
			} catch (Exception e) {

			}

			if (indice_vide == 1
					&& (buttonVal[0][0] != buttonVal[0][1]
							&& buttonVal[0][0] != buttonVal[0][3]
							&& buttonVal[0][1] != buttonVal[0][2]
							&& buttonVal[0][1] != buttonVal[0][4]
							&& buttonVal[0][2] != buttonVal[0][5]
							&& buttonVal[0][3] != buttonVal[0][4]
							&& buttonVal[0][3] != buttonVal[0][6]
							&& buttonVal[0][4] != buttonVal[0][5]
							&& buttonVal[0][4] != buttonVal[0][7]
							&& buttonVal[0][5] != buttonVal[0][8]
							&& buttonVal[0][6] != buttonVal[0][7] && buttonVal[0][7] != buttonVal[0][8])) {
				// GAME OVER
				Log.d(DEBUG_TAG, "Game Over !");
				GameOver();
			}

			return false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	public void onRightUpSwipe() {
		if (blockActif == 3)
			add2Blocks(3, 2);
		Log.d(DEBUG_TAG, "onRightUpSwipe: ");
	}

	public void onLeftDownSwipe() {
		if (blockActif == 2)
			add2Blocks(2, 3);
		Log.d(DEBUG_TAG, "onLeftDownSwipe: ");

	}

	public void onRightDownSwipe() {
		if (blockActif == 1)
			add2Blocks(1, 4);
		Log.d(DEBUG_TAG, "onRightDownSwipe: ");

	}

	public void onLeftUpSwipe() {
		if (blockActif == 4)
			add2Blocks(4, 1);
		Log.d(DEBUG_TAG, "onLeftUpSwipe: ");

	}

	public void onUpSwipe() {
		switch (gameselect) {
		case FourSquare:
			if (blockActif == 3)
				add2Blocks(3, 1);
			else if (blockActif == 4)
				add2Blocks(4, 2);
			break;
		case OneSquare:
			AddOneSquare(Direction.up);
			break;
		}
	}

	public void onDownSwipe() {
		switch (gameselect) {
		case FourSquare:
			if (blockActif == 1)
				add2Blocks(1, 3);
			else if (blockActif == 2)
				add2Blocks(2, 4);
			break;
		case OneSquare:
			AddOneSquare(Direction.down);
			break;
		}
	}

	public void onRightSwipe() {
		switch (gameselect) {
		case FourSquare:
			if (blockActif == 1)
				add2Blocks(1, 2);
			else if (blockActif == 3)
				add2Blocks(3, 4);
			break;
		case OneSquare:
			AddOneSquare(Direction.right);
			break;
		}
	}

	public void onLeftSwipe() {
		switch (gameselect) {
		case FourSquare:
			if (blockActif == 2)
				add2Blocks(2, 1);
			else if (blockActif == 4)
				add2Blocks(4, 3);
			break;
		case OneSquare:
			AddOneSquare(Direction.left);
			break;
		}
	}

	/**
	 * On additionne à l'intérieur les lettres comme pour 2048, ainsi pour up en
	 * montant la ligne 3 on vérifie qu'il n'y a pas de combinaison On remets à
	 * zéro les lettres si lignes/colonnes ou diagonales avec 3 même lettres On
	 * fait apparaitre soit un A si pas de lignes/colonnes/diagonales disparues
	 * ou sinon on fait apparaitre la lettre supérieure => AAA mettre un B ou
	 * YYY mettre un Z
	 * 
	 * @param blockActif2
	 * @param up
	 */
	private boolean AddOneSquare(Direction dir) {
		Integer[] valNewBlock = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		Integer[] flag = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Integer[] vide = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Integer[] valOldBlock = buttonVal[0].clone();
		int tempscore = 1;
		int temp;
		int j = 0;

		switch (dir) {

		case down:
			for (int i = 0; i < 3; i++) {
				temp = valOldBlock[i];
				valOldBlock[i] = valOldBlock[i + 6];
				valOldBlock[i + 6] = temp;
			}
			break;
		case right:
			temp = valOldBlock[6];
			valOldBlock[6] = valOldBlock[0];
			valOldBlock[0] = valOldBlock[2];
			valOldBlock[2] = valOldBlock[8];
			valOldBlock[8] = temp;
			temp = valOldBlock[7];
			valOldBlock[7] = valOldBlock[3];
			valOldBlock[3] = valOldBlock[1];
			valOldBlock[1] = valOldBlock[5];
			valOldBlock[5] = temp;
			break;
		case left:
			temp = valOldBlock[2];
			valOldBlock[2] = valOldBlock[0];
			valOldBlock[0] = valOldBlock[6];
			valOldBlock[6] = valOldBlock[8];
			valOldBlock[8] = temp;
			temp = valOldBlock[5];
			valOldBlock[5] = valOldBlock[1];
			valOldBlock[1] = valOldBlock[3];
			valOldBlock[3] = valOldBlock[7];
			valOldBlock[7] = temp;
			break;
		case up:
		default:
			break;
		}
		// colonnes (0,3,6), (1,4,7) et (2,5,8)
		for (int i = 0; i < 3; i++) {
			// cas 00A ou 0A0
			if ((valOldBlock[i] == 0 && valOldBlock[i + 3] == 0 && valOldBlock[i + 6] != 0)
					|| (valOldBlock[i] == 0 && valOldBlock[i + 6] == 0 && valOldBlock[i + 3] != 0))
				valNewBlock[i] = valOldBlock[i + 3] + valOldBlock[i + 6];

			// cas 0AA ou A0A ou AA0
			else if ((valOldBlock[i] == 0
					&& valOldBlock[i + 3] == valOldBlock[i + 6] && valOldBlock[i + 3] != 0)
					|| (valOldBlock[i + 3] == 0
							&& valOldBlock[i] == valOldBlock[i + 6] && valOldBlock[i] != 0)
					|| (valOldBlock[i + 6] == 0
							&& valOldBlock[i] == valOldBlock[i + 3] && valOldBlock[i] != 0)) {
				valNewBlock[i] = valOldBlock[i + 6] == 0 ? valOldBlock[i] + 1
						: valOldBlock[i + 6] + 1;

			}

			// cas 0AB ou A0B
			else if ((valOldBlock[i] == 0
					&& valOldBlock[i + 3] != valOldBlock[i + 6]
					&& valOldBlock[i + 3] != 0 && valOldBlock[i + 6] != 0)
					|| (valOldBlock[i + 3] == 0
							&& valOldBlock[i] != valOldBlock[i + 6]
							&& valOldBlock[i] != 0 && valOldBlock[i + 6] != 0)) {
				valNewBlock[i] = valOldBlock[i + 3] + valOldBlock[i];
				valNewBlock[i + 3] = valOldBlock[i + 6];
			}
			// cas CAA
			else if (valOldBlock[i] != 0
					&& valOldBlock[i + 3] == valOldBlock[i + 6]
					&& valOldBlock[i + 6] != 0) {
				valNewBlock[i] = valOldBlock[i];
				valNewBlock[i + 3] = valOldBlock[i + 3] + 1;

			}
			// cas AAC
			else if (valOldBlock[i + 6] != 0
					&& valOldBlock[i] == valOldBlock[i + 3]
					&& valOldBlock[i] != 0) {
				valNewBlock[i + 3] = valOldBlock[i + 6];
				valNewBlock[i] = valOldBlock[i] + 1;
			} else {
				valNewBlock[i] = valOldBlock[i];
				valNewBlock[i + 3] = valOldBlock[i + 3];
				valNewBlock[i + 6] = valOldBlock[i + 6];
				j++;
			}
			valNewBlock[i] = (valNewBlock[i] != 0 && valNewBlock[i] % MAX == 0) ? valNewBlock[i] + 1
					: valNewBlock[i];
			valNewBlock[i + 3] = (valNewBlock[i + 3] != 0 && valNewBlock[i + 3]
					% MAX == 0) ? valNewBlock[i + 3] + 1 : valNewBlock[i + 3];
		}

		switch (dir) {
		case down:
			for (int i = 0; i < 3; i++) {
				temp = valNewBlock[i];
				valNewBlock[i] = valNewBlock[i + 6];
				valNewBlock[i + 6] = temp;
			}
			break;
		case left:
			temp = valNewBlock[6];
			valNewBlock[6] = valNewBlock[0];
			valNewBlock[0] = valNewBlock[2];
			valNewBlock[2] = valNewBlock[8];
			valNewBlock[8] = temp;
			temp = valNewBlock[7];
			valNewBlock[7] = valNewBlock[3];
			valNewBlock[3] = valNewBlock[1];
			valNewBlock[1] = valNewBlock[5];
			valNewBlock[5] = temp;
			break;
		case right:
			temp = valNewBlock[2];
			valNewBlock[2] = valNewBlock[0];
			valNewBlock[0] = valNewBlock[6];
			valNewBlock[6] = valNewBlock[8];
			valNewBlock[8] = temp;
			temp = valNewBlock[5];
			valNewBlock[5] = valNewBlock[1];
			valNewBlock[1] = valNewBlock[3];
			valNewBlock[3] = valNewBlock[7];
			valNewBlock[7] = temp;
			break;
		case up:
		default:
			break;
		}
		// les trois colonnes n'ont pas bougé
		if (j == 3)
			return false;
		// récupération de la plus petite valeur avant alignement
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++) {
			min = (valNewBlock[i] < min && valNewBlock[i] != 0) ? valNewBlock[i]
					: min;
		}
		// on vérifie les alignements
		score += VerifSquare(valNewBlock, flag);
		for (int i = 0; i < 9; i++) {
			buttonVal[0][i] = flag[i] == 1 ? 0 : valNewBlock[i];
			buttonsAZgame[0][i].setTypeface(null, Typeface.NORMAL);
			buttonsAZgame[0][i].setTextColor(getResources().getColor(
					R.color.Black));
			buttonsAZgame[0][i].setText(""
					+ ((buttonVal[0][i] == 0) ? "" : (char) (buttonVal[0][i]
							.intValue() % MAX + 64)));
		}
		// on récupére les espace vide
		indice_vide = 0;
		for (int i = 0; i < 9; i++) {
			if (buttonVal[0][i] == 0) {
				vide[indice_vide++] = i;
			}
		}

		// on mélange les indice des espaces vides
		for (int i = 0; i < indice_vide; i++) {
			int newWhere = new Random().nextInt(indice_vide);
			temp = vide[i];
			vide[i] = vide[newWhere];
			vide[newWhere] = temp;
		}
		// on teste que l'ajout d'un jeton dans un espace vide ne crée pas
		// d'alignement
		int i = 0;
		for (i = 0; i < indice_vide; i++) {
			valNewBlock = buttonVal[0].clone();
			valNewBlock[vide[i]] = min;
			if (VerifSquare(valNewBlock, flag) == 0)
				break;
			else // on teste que l'on a plus de choix sans alignement on va donc
					// prendre un autre jeton
			if (i == indice_vide - 1) {
				min = (min + 1) % MAX == 0 ? (min + 2) : (min + 1);
				i--;
			}
		}

		// on ajoute le nouveau jeton
		buttonVal[0] = valNewBlock.clone();
		buttonsAZgame[0][vide[i]].setTypeface(null, Typeface.BOLD);
		buttonsAZgame[0][vide[i]].setTextColor(getResources().getColor(
				R.color.Red));
		buttonsAZgame[0][vide[i]].setText(""
				+ (char) (buttonVal[0][vide[i]].intValue() % MAX + 64));

		textScore.setText("Score=" + score);
		level = 0;
		for (int l = 0; l < 9; l++) {
			level = buttonVal[0][l] / MAX > level ? buttonVal[0][l] / MAX
					: level;
		}
		textLevel.setText("Level=" + (level + 1));
		Log.d(DEBUG_TAG, "indice_vide= " + Integer.toString(indice_vide));
		return true;

	}

	/**
	 * On additionne les cases de b1+b2 dans b2 si aucun élément devient > "Z"
	 * (26+64) Remettre à zero les lignes égales ou diagonales On mets par le
	 * block b2 en couleur Active et b1 en désactif on retourne true si ok sinon
	 * on touche à rien et on retourne false
	 * 
	 * @param b1
	 * @param b2
	 */
	private boolean add2Blocks(int b1, int b2) {

		Integer[] valNewBlock = new Integer[9];
		Integer[] flag = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Integer[] ordre = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

		for (int i = 0; i < 9; i++) {
			valNewBlock[i] = buttonVal[b1 - 1][i] + buttonVal[b2 - 1][i];
			if (valNewBlock[i] + 64 > 90) {
				buttonsAZgame[b1 - 1][i].setBackgroundColor(getResources()
						.getColor(R.color.Red));
				return false;
			}
		}

		score += VerifSquare(valNewBlock, flag);
		// on trie les cases du nouveau bloc
		boolean tri_bulle = true;
		int xswitch = 0;
		while (tri_bulle)
			for (int i = 0; i < 8; i++) {
				if (valNewBlock[ordre[i]] > valNewBlock[ordre[i + 1]]) {
					xswitch = ordre[i];
					ordre[i] = ordre[i + 1];
					ordre[i + 1] = xswitch;
					tri_bulle = true;
				} else
					tri_bulle = false;
			}

		int newWhere = new Random().nextInt(difficulte);
		newWhere = ordre[newWhere];
		for (int i = 0; i < 9; i++) {

			buttonsAZgame[b1 - 1][i]
					.setBackgroundDrawable(defaultButtonDrawable);
			buttonVal[b1 - 1][i] = (newWhere == i) ? 1 : 0;
			buttonsAZgame[b1 - 1][i].setText(""
					+ ((buttonVal[b1 - 1][i] == 0) ? ""
							: (char) (buttonVal[b1 - 1][i].intValue() + 64)));
			buttonVal[b2 - 1][i] = flag[i] == 1 ? 0 : valNewBlock[i];
			buttonsAZgame[b2 - 1][i].setText(""
					+ ((buttonVal[b2 - 1][i] == 0) ? ""
							: (char) (buttonVal[b2 - 1][i].intValue() + 64)));
		}

		String str = "TableLayoutLocal" + b1;
		try {
			TableLayout j = (TableLayout) findViewById((Integer) R.id.class
					.getField(str).get(new R()));
			j.setBackgroundColor(getResources()
					.getColor(R.color.tabTransparent));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		str = "TableLayoutLocal" + b2;
		try {
			TableLayout j = (TableLayout) findViewById((Integer) R.id.class
					.getField(str).get(new R()));
			j.setBackgroundColor(getResources().getColor(R.color.Aqua));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		blockActif = b2;

		textScore.setText("Score=" + score);
		return true;

	}

	int VerifSquare(Integer[] valNewBlock, Integer[] flag) {

		int nbalign = 0;
		// vérif ligne1
		if (valNewBlock[0] == valNewBlock[1]
				&& valNewBlock[1] == valNewBlock[2]) {
			flag[0] = 1;
			flag[1] = 1;
			flag[2] = 1;
			nbalign += valNewBlock[0].intValue();
		}
		// vérif ligne2
		if (valNewBlock[3] == valNewBlock[4]
				&& valNewBlock[4] == valNewBlock[5]) {
			flag[3] = 1;
			flag[4] = 1;
			flag[5] = 1;
			nbalign += valNewBlock[3].intValue();
		}
		// vérif ligne3
		if (valNewBlock[6] == valNewBlock[7]
				&& valNewBlock[7] == valNewBlock[8]) {
			flag[6] = 1;
			flag[7] = 1;
			flag[8] = 1;
			nbalign += valNewBlock[6].intValue();
		}
		// vérif colonne1
		if (valNewBlock[0] == valNewBlock[3]
				&& valNewBlock[3] == valNewBlock[6]) {
			flag[0] = 1;
			flag[3] = 1;
			flag[6] = 1;
			nbalign += valNewBlock[0].intValue();
		}
		// vérif colonne2
		if (valNewBlock[1] == valNewBlock[4]
				&& valNewBlock[4] == valNewBlock[7]) {
			flag[1] = 1;
			flag[4] = 1;
			flag[7] = 1;
			nbalign += valNewBlock[1].intValue();
		}
		// vérif colonne3
		if (valNewBlock[2] == valNewBlock[5]
				&& valNewBlock[5] == valNewBlock[8]) {
			flag[2] = 1;
			flag[5] = 1;
			flag[8] = 1;
			nbalign += valNewBlock[2].intValue();
		}
		// vérif diagonal gauche vers droite
		if (valNewBlock[0] == valNewBlock[4]
				&& valNewBlock[4] == valNewBlock[8]
				&& gameselect == ListeOfGame.FourSquare) {
			flag[0] = 1;
			flag[4] = 1;
			flag[8] = 1;
			nbalign += valNewBlock[0].intValue();
		}
		// vérif diagonal droite vers gauche
		if (valNewBlock[2] == valNewBlock[4]
				&& valNewBlock[4] == valNewBlock[6]
				&& gameselect == ListeOfGame.FourSquare) {
			flag[2] = 1;
			flag[4] = 1;
			flag[6] = 1;
			nbalign += valNewBlock[2].intValue();
		}
		return nbalign;
	}

	// Gestion du Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void Aide(MenuItem item) {
		AlertDialog.Builder alertAide = null;
		bPause = true;
		alertAide = new AlertDialog.Builder(this);
		alertAide.setTitle("Aide - Règles du Jeu")
				.setMessage(getString(R.string.aide))
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						bPause = false;
					}
				});
		TextView textView = (TextView) alertAide.show().findViewById(
				android.R.id.message);
		textView.setTextSize(14);
	}

	// Gestion de la vie l'activité (cf onCreate)
	@Override
	public void onPause() {
		super.onPause(); // Always call the superclass method first
		bPause = true;
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first
		bPause = false;
		try {
			// Check Google Play Service Available
			checkGooglePlayServicesAvailable(this);
		} catch (Throwable e) {
			Log.e("Error in OnResume during 'checkGooglePlayServicesAvailable': GooglePlayServiceUtil: ",
					"" + e);
		}
	}

	boolean checkGooglePlayServicesAvailable(final AZgameActivity p) {
		final int connectionStatusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(p);
		Log.i(DEBUG_TAG,
				"checkGooglePlayServicesAvailable, connectionStatusCode="
						+ connectionStatusCode);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			if (connectionStatusCode == SERVICE_MISSING) {
				showGooglePlayServicesAvailabilityErrorDialog(
						connectionStatusCode, this);

				return false;
			}
		}
		p.runOnUiThread(new Runnable() {
			public void run() {
				Toast t = Toast.makeText(p, "Google Play Service available",
						Toast.LENGTH_SHORT);
				t.show();
			}
		});
		return true;
	}

	void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode, final AZgameActivity p) {
		p.runOnUiThread(new Runnable() {
			public void run() {
				final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, p, REQUEST_GOOGLE_PLAY_SERVICES);
				if (dialog == null) {
					Log.e(DEBUG_TAG,
							"couldn't get GooglePlayServicesUtil.getErrorDialog");
					Toast.makeText(p,
							"Google Play Services Version incompatible...",
							Toast.LENGTH_LONG).show();
				}
				dialog.show();
			}
		});
	}

	private void verifyWebHighScore() {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					HSWeb = Integer.parseInt(new FTPConnect().readfile(
							"azgame.txt", "hs"));
					HSWebName = new FTPConnect().readfile("azgame.txt", "name");
					HSWebLevel = Integer.parseInt(new FTPConnect().readfile(
							"azgame.txt", "level"));
				} catch (NumberFormatException e) {
					HSWeb = -1;
				}
				if (HSWeb < HSLocal && HSWeb != -1) {
					final String result = new String(new FTPConnect()
							.writefile(
									"azgame.txt",
									new String[] { "name", HSLocalName },
									new String[] { "hs",
											Integer.toString(HSLocal) },
									new String[] { "level",
											Integer.toString(HSLocalLevel) }));

					ma.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(ma, result, Toast.LENGTH_SHORT)
									.show();
						}
					});
					if (!result.startsWith("!"))
						HSWeb = HSLocal;
					HSWebName = new String(HSLocalName);
					HSWebLevel = HSLocalLevel;
				}
				ma.runOnUiThread(new Runnable() {
					public void run() {
						textHSLocal = (TextView) findViewById(R.id.textHSWeb);
						if (HSWeb != -1) {
							textHSLocal.setText("Web High-Score="
									+ Integer.toString(HSWeb) + ", Level="
									+ Integer.toString(HSWebLevel) + " by "
									+ HSWebName + "");
						} else {
							textHSLocal
									.setText("Web High-Score=!No Internet Connexion!");
						}
					};
				});
			};
		}).start();

	}

	void getLocalHighScore() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		HSLocalName = preferences.getString("name", "Unknown");
		HSLocalLevel = preferences.getInt("level", 0);
		HSLocal = preferences.getInt("highscore", 0);
		textHSLocal = (TextView) findViewById(R.id.textHSLocal);
		textHSLocal.setText("Local High-Score=" + Integer.toString(HSLocal)
				+ ", Level=" + Integer.toString(HSLocalLevel) + " by "
				+ HSLocalName + "");

	}

	void setLocalHighScore() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		editor.putString("name", HSLocalName);
		editor.putInt("highscore", HSLocal);
		editor.putInt("level", HSLocalLevel);
		editor.commit();
		textHSLocal = (TextView) findViewById(R.id.textHSLocal);
		textHSLocal.setText("Local High-Score=" + Integer.toString(HSLocal)
				+ ", Level=" + Integer.toString(HSLocalLevel) + " by "
				+ HSLocalName + "");
	}
}
