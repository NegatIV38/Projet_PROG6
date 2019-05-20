package Controller;
import java.util.Random;
import java.util.*;

import java.lang.reflect.*;
import javafx.util.Pair;
import Modele.*;
import java.lang.Character;
public class IA_Controller extends Controller {

	String m_hname;
	private final int m_depth = 4;
	Random r;
	List<Grille> m_memory;

	public IA_Controller(int player,String hname){
		super(player);
		m_hname = hname;
		r = new Random();
		m_memory = new ArrayList<Grille>();
	}
	public IA_Controller(int player, Partie game,String hname){
		super(player,game);
		m_hname = hname;
		r = new Random();
		m_memory = new ArrayList<Grille>();
	}

	static double h0(int nbPL,int nbADV){
		return 1/Math.exp(nbADV);
	}
	static double h4(int nbPL,int nbADV){
		return 2*h0(nbPL,nbADV);
	}
	static double h10(int nbPL,int nbADV){
		return 4*h0(nbPL,nbADV);
	}
	static double h17(int nbPL,int nbADV){
		return h0(nbPL,nbADV) - 1/Math.exp(nbPL);
	}
	static double h18(int nbPL,int nbADV){
		return h0(nbPL,nbADV) - 1/Math.exp(nbPL-2);
	}
 	static double h19(int nbPL,int nbADV){
		return h0(nbPL,nbADV) - 1/Math.exp(nbPL-1);
	}
	static double h20(int nbPL,int nbADV){
		return 5/Math.exp(nbADV)-2/Math.exp(nbPL-1);
	}
	static double h21(int nbPL,int nbADV){
		return 5/Math.exp(nbADV)-3/Math.exp(nbPL-2);
	}
	static double h22(int nbPL,int nbADV){
		return 5*(1/Math.exp(nbADV)) + nbPL + 0 /* 0.5*(1/Math.exp(nbCapADV)) + 0.25*nbCapPL*/;
	}
	static double h23(int nbPL,int nbADV){
		return 2*h0(nbPL,nbADV) + 2*(nbPL/nbADV);
	}
	static double h24(int nbPL,int nbADV){
		return 20*h0(nbPL,nbADV) + 2*(nbPL/nbADV);
	}
	static double h25(int nbPL,int nbADV){
		return 100*h0(nbPL,nbADV) + 100*(nbPL/nbADV);
	}
	static double h26(int nbPL,int nbADV){
		return 100*h0(nbPL,nbADV) + 10*((nbPL-1.3)/(nbADV+0.4));
	}
	
	static double h27(int nbPL,int nbADV){
		return 100*h0(nbPL,nbADV) + 10*((nbPL-1.3)/(nbADV+0.4)) + 30*(1./(nbPL+nbADV));
	}

	public double heuristique(String hname,int nbPL, int nbADV){
		Method m;
		double ret = -1;
		try{
			m = IA_Controller.class.getDeclaredMethod(hname,int.class,int.class);
			ret= (double)m.invoke(this,nbPL,nbADV);
		}
		catch(NoSuchMethodException e){
		}
		catch(IllegalAccessException e){
		}
		catch(InvocationTargetException e){
		}
		//System.out.println("fit : " + ret);
		return ret;
	}
	
	@Override
	public boolean jouer(Coup a){
		if(a != null){
			return m_game.jouer(a);
		}
		else{
			return false;
		}
	}
	//Note une configuration	
	public double fitness(Partie state,int player){
		Grille config = state.grille();
		int nbPL = 0, nbADV = 0;//, nbCapturablePL = 0,nbCapturableADV = 0;
		
		for(int i = 0; i < config.ligne(); i++){
			for(int j =0; j < config.colonne(); j++){
				if(config.at(i,j) == (player==1?1:2)){
					nbPL++;	
					/*List<Coordonnees> adjs = state.casesAccessibles(i,j);

					for(int id = 0; id < adjs.size(); id++){
						Coordonnees dir = new Coordonnees(adjs.get(id).ligne()-i,adjs.get(id).colonne()-j);
						int is,js;
						is = i+2*dir.ligne(); 
						js = j+2*dir.colonne();
						while(config.caseExiste(is,js) && config.at(is,js) == (player==1?2:1)){
							nbCapturablePL++;
							is+= dir.ligne();
							js+= dir.colonne();
						}
						is = i - dir.ligne();
						js = j - dir.colonne();
						while(config.caseExiste(is,js) && config.at(is,js) == (player==1?2:1)){
							nbCapturablePL++;
							is -= dir.ligne();
							js -= dir.colonne();
						}
					}*/


						
				}
				if(config.at(i,j) == (player==1?2:1)){
					nbADV++;
					/*List<Coordonnees> adjs = state.casesAccessibles(i,j);

					for(int id = 0; id < adjs.size(); id++){
						Coordonnees dir = new Coordonnees(adjs.get(id).ligne()-i,adjs.get(id).colonne()-j);
						int is,js;
						is = i+2*dir.ligne(); 
						js = j+2*dir.colonne();
						while(config.caseExiste(is,js) && config.at(is,js) == (player==1?1:2)){
							nbCapturableADV++;
							is+= dir.ligne();
							js+= dir.colonne();
						}
						is = i - dir.ligne();
						js = j - dir.colonne();
						while(config.caseExiste(is,js) && config.at(is,js) == (player==1?1:2)){
							nbCapturableADV++;
							is -= dir.ligne();
							js -= dir.colonne();
						}
					}*/

				}
				
			}
		}
			
		//state.afficher();
		if(nbADV ==0){
			return Double.MAX_VALUE-1;
		}
		if(nbPL == 0){
			return -Double.MAX_VALUE+1;
		}

		double fit = heuristique(m_hname,nbPL,nbADV);
		return fit;
	}

	public Pair<Double,Integer> minimax(Partie state,int horizon,int player){
		MutableDouble alpha = new MutableDouble(-Double.MAX_VALUE); //val MAX
		MutableDouble beta = new MutableDouble(Double.MAX_VALUE);//val MIN
		m_memory.clear();
		return minimax(state,horizon,player,"R",alpha,beta);
	}

	private Pair<Double,Integer> minimax(Partie state,int horizon,int player,String tree,MutableDouble alpha, MutableDouble beta){

		if(state.gagnant() != 0 || horizon == 0){
			double ret = fitness(state,player);
			//System.out.println(tree +"("+(player==m_player?"MAX":"MIN")+") = " + ret  + "\tA : " + alpha.getValue() + "\tB : " + beta.getValue());
			//state.afficher();

			return new Pair<Double,Integer>(ret,-2);
		}
		List<Coup> actions = state.listeCoupsValides() ;
		if(actions.size() == 0){
			return new Pair<Double,Integer>(-Double.MAX_VALUE+1,-3);
		}
		double ret;
		if(player == m_player){
			ret = -Double.MAX_VALUE;
		}else{
			ret = Double.MAX_VALUE; 
		}
		int id = 0;
		List<Integer> possibilities = new ArrayList<Integer>();		
		String strtree ="";

		for(int i = 0; i < actions.size(); i++){
			double oldret = ret;

			strtree = tree;
			if(Character.isLetter(tree.charAt(tree.length()-1))){
				strtree += String.valueOf(i); 
			}
			else{
				strtree += (char)('A'+i);
			}
			
			state.jouer(actions.get(i));
			//memoisation
			Grille unique = state.getUniqueState();
			if(m_memory.contains(unique)){
				break;
			} 
			else{
				m_memory.add(unique);
			}
			int nextPlayer = (state.joueur()==1?1:-1);
			double value = minimax(state,(nextPlayer==player?horizon:horizon-1),nextPlayer,strtree,alpha,beta).getKey();
			state.annuler();
			if(player == m_player){
				ret = Math.max(ret,value);
				if(nextPlayer != player){
					if(beta.getValue() != Double.MAX_VALUE && value > beta.getValue()){
						//System.out.println(tree +"("+(player==m_player?"MAX":"MIN")+") = " + ret  + "\tA : " + alpha.getValue() + "\tB : " + beta.getValue());
						//state.afficher();
						if(ret != oldret){
							id = i;
							possibilities.clear();
							possibilities.add(id);
						}
						else if(ret == value){
							possibilities.add(i);
						}
						break;
						//return new Pair<Double,Integer>(ret,i);
					}
					alpha.setValue(Math.max(alpha.getValue(),ret));
				}
			}else{
				ret = Math.min(ret,value);
				if(nextPlayer != player){
					if(alpha.getValue() != -Double.MAX_VALUE && ret < alpha.getValue()){
						//System.out.println(tree +"("+(player==m_player?"MAX":"MIN")+") = " + ret  + "\tA : " + alpha.getValue() + "\tB : " + beta.getValue());
						//state.afficher();
						
						if(ret != oldret){
							id = i;
							possibilities.clear();
							possibilities.add(id);
						}
						else if(ret == value){
							possibilities.add(i);
						}	
						break;
						//return new Pair<Double,Integer>(ret,i);
					}
					beta.setValue(Math.min(beta.getValue(),ret));
				}
				
			}
			if(ret != oldret){
				id = i;
				possibilities.clear();
				possibilities.add(id);
			}
			else if(ret == value){
				possibilities.add(i);
			}
		}
		if(possibilities.size() > 1){
			id = possibilities.get(r.nextInt(possibilities.size()));
		}
		//System.out.println(tree +"("+(player==m_player?"MAX":"MIN")+") = " + ret  + "\tA : " + alpha.getValue() + "\tB : " + beta.getValue());
		//state.afficher();
		return new Pair<Double,Integer>(ret,id);
	}

	public Coup think(int level){
		if(level == 0){
			List<Coup> ret = m_game.listeCoupsValides() ;
			Random gen = new Random();
			if(ret.size() > 0){
				return ret.get(gen.nextInt(ret.size()));
			}
			else{
				System.out.println("No actions");
				m_game.finTour();
				return null;
			}
		}
		if(level == 1){
			List<Coup> ret = m_game.listeCoupsValides() ;
			Pair<Double,Integer> id = minimax(new Partie(m_game),m_depth,m_player);
			if(id.getValue() <0){
				System.out.println("IA : No actions");
			}
			//System.out.println("p : " + m_player +"  max : " + id.getKey());
			return ret.get(id.getValue());
		}
		return null;
	}


}

class MutableDouble {
  private double value;

	public MutableDouble(double v){
		value = v;
	}

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

}


