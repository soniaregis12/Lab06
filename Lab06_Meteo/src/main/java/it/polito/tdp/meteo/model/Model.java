package it.polito.tdp.meteo.model;

import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO md;
	private List<Rilevamento> rilevamenti;
	
	private int costoMinimo;
	private String soluzioneBest = "";
	

	public Model() {
		md = new MeteoDAO();
		rilevamenti = null;
	}

	
	public String getUmiditaMedia(int mese) {
		
		String pippo = "";
		int contatore = 0;
		int somma = 0;
		
		List<Rilevamento> rilevamentiMilano = md.getAllRilevamentiLocalitaMese(mese, "Milano");
		List<Rilevamento> rilevamentiGenova = md.getAllRilevamentiLocalitaMese(mese, "Genova");
		List<Rilevamento> rilevamentiTorino = md.getAllRilevamentiLocalitaMese(mese, "Torino");
		
		for(Rilevamento r : rilevamentiMilano) {
			contatore++;
			somma += r.getUmidita();
		}
		pippo = "Umidità media Milano: " + somma/contatore + "\n";
		
		contatore = 0;
		somma = 0;
		
		for(Rilevamento r : rilevamentiGenova) {			
			contatore++;
			somma += r.getUmidita();
		}
		pippo += "Umidità media Genova: " + somma/contatore + "\n";
		
		contatore = 0;
		somma = 0;
		
		for(Rilevamento r : rilevamentiTorino) {		
			contatore++;
			somma += r.getUmidita();
		}
		pippo += "Umidità media Torino: " + somma/contatore;
		
		return pippo;
	}
	
	
	public String trovaSequenza(int mese) {
		
		this.rilevamenti = md.getAllRilevamentiPrimaQuindicina(mese);
		
		return "TODO!";
	}
	
	public void ricorsiva(Set<Rilevamento> parziale, int livello) {
		
		int costo = calcoloCosto(parziale);
		
		if(costo >= costoMinimo) {	//Non potrò mai avere una soluzione migliore, quindi finisco
			return;
		}
		
		if(parziale.size() == NUMERO_GIORNI_TOTALI ) {
			// Ci troviamo nel caso terminale, dobbiamo vedere se la soluzione rispetta i parametri
			
			if(giorniMaxNonSuperati(parziale) && giorniMinimiFatti(parziale)) {
				for(Rilevamento r : parziale) {
					this.soluzioneBest += r.getLocalita() + "\n"; 
				}
			}
		}
		// In tutti gli altri casi io devo aggiungere una possibilità, o non aggiungerla e andare a perlustrare tutti i sotto-casi
		// Se non ho superato i giorni massimi posso mettere quello di prima
		
		// Se ho fatto i giorni minimi posso cambiare città
	}


	private boolean giorniMinimiFatti(Set<Rilevamento> parziale) {
		
		String precedente = "";
		int contatore = 0;
		
		for(Rilevamento r : parziale) {
			if(precedente == "") {
				contatore++;
			}
			if(r.getLocalita().equals(precedente)) {
				contatore++;
			}
			if(contatore < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && (!r.getLocalita().equals(precedente) || precedente != "")) {
				return false;
			}
			if(contatore >= NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && !r.getLocalita().equals(precedente)) {
				contatore = 1;
			}
			precedente = r.getLocalita();
		}
		return true;
	}


	private boolean giorniMaxNonSuperati(Set<Rilevamento> parziale) {
		
		int contatoreMilano = 0;
		int contatoreGenova = 0;
		int contatoreTorino = 0;
		
		for(Rilevamento r : parziale) {
			if(r.getLocalita().equals("Milano")) {
				contatoreMilano++;
			}
			if(r.getLocalita().equals("Genova")) {
				contatoreGenova++;
			}
			if(r.getLocalita().equals("Torino")) {
				contatoreTorino++;
			}
		}
		return contatoreGenova <= NUMERO_GIORNI_CITTA_MAX && contatoreMilano <= NUMERO_GIORNI_CITTA_MAX && contatoreTorino <= NUMERO_GIORNI_CITTA_MAX;
	}


	private int calcoloCosto(Set<Rilevamento> parziale) {
		
		int pippo = 0;
		String localitaPrecedente = "";
		
		for(Rilevamento r : parziale) {
			
			if( localitaPrecedente != "" && !r.getLocalita().equals(localitaPrecedente)) {
				pippo += COST;
			}
			pippo += r.getUmidita();
			localitaPrecedente = r.getLocalita();
		}
		return pippo;
	}
	

}
