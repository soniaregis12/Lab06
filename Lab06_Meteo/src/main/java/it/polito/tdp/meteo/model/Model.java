package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO md;
	
	private double costoMinimo;
	private List<Citta> leCitta;
	private List<Citta> best;

	public Model() {
		md = new MeteoDAO();
		this.leCitta = md.getAllCitta();
	}

	
	public String getUmiditaMedia(int mese) {
		
		String pippo = "";
		double umidita;
	
		for(Citta c : leCitta) {
			umidita = md.getUmiditaMedia(c.getNome(), mese);
			pippo += "Umidità media " + c + ": " + umidita + "\n";
		}
		return pippo;
	}
	
	
	public List<Citta> trovaSequenza(int mese) {
		
		List<Citta> parziale = new ArrayList<Citta>();
		this.best = new ArrayList<Citta>();
		
		for(Citta c : leCitta) {
			c.setRilevamenti(md.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		ricorsiva(parziale, 0);
		
		return this.best;
	}
	
	public void ricorsiva(List<Citta> parziale, int livello) {
		
		if(livello == NUMERO_GIORNI_TOTALI) {
			// Ci troviamo nel caso terminale
			Double costo = calcoloCosto(parziale);
			if(best == null || calcoloCosto(best) > costo) {
				this.best = new ArrayList<Citta>(parziale);
			}
			//System.out.println(parziale);
			System.out.println(this.best);
		}else {
			for(Citta prova : leCitta) {
				if(aggiuntavalida(parziale, prova)) {
					parziale.add(prova);
					ricorsiva(parziale, livello+1);
					parziale.remove(parziale.size()-1);	
				}
			}
		}
		
	}


	private boolean aggiuntavalida(List<Citta> parziale, Citta prova) {
		int contatoreCitta = 1;
		for(Citta c : parziale) {
			if(c.equals(prova)) {
				contatoreCitta++;
			}
		}
		if(contatoreCitta > NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		
		if(parziale.size() == 0) {
			return true;
		}
		if(parziale.size() == 1 || parziale.size() == 2) {
			return parziale.get(parziale.size()-1).equals(prova);
		}
		
		if (parziale.get(parziale.size()-1).equals(prova))
			return true; 
		
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
		&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true;
			
		return false;
	}

	private Double calcoloCosto(List<Citta> parziale) {
		
		Double pippo = 0.0;
	
		for(int giorno=0; giorno < NUMERO_GIORNI_TOTALI; giorno++) {
			Citta c = parziale.get(giorno);
			double umidita = c.getRilevamenti().get(giorno).getUmidita();
			pippo += umidita;
		}
		for(int giorno=1; giorno<NUMERO_GIORNI_TOTALI; giorno++) {
			if(!parziale.get(giorno).equals(parziale.get(giorno-1))) {
				pippo += COST;
			}
		}
		return pippo;
	}
	

}
