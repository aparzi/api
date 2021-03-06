package it.unimol.my.login;

import it.unimol.my.config.ConfigurationManager;
import it.unimol.my.requesterhtml.HTMLRequesterException;
import it.unimol.my.requesterhtml.HTMLRequesterInterface;
import it.unimol.my.requesterhtml.HTMLRequesterManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.exceptions.UnirestException;
import it.unimol.my.utils.StringUtils;

/**
 * Classe che fa il parser per il login di esse3 Unimol
 *
 * @author Ivan Di Rienzo
 */
public class UnimolLoginParser implements LoginParser {
	private static final Logger logger = Logger.getLogger(UnimolLoginParser.class.getName());
	
	@Override
	public UserInformation getLoginInformation(String username, String password) throws UnirestException {
		return this.getLoginInformation(username, password, null);
	}
    
    public UserInformation getLoginInformation(String username, String password, String careerId) throws UnirestException {

    	HTMLRequesterInterface requester;
    	try {
    		requester = HTMLRequesterManager.getManager().getInstance(username, password, careerId);
    	} catch (HTMLRequesterException e) {
    		logger.log(Level.SEVERE, e.getMessage(), e);
    		throw new UnirestException(e.getMessage());
    	}
    	
        try {
        	URL url = new URL(ConfigurationManager.getInstance().getLogonUrl());
            String resPage = requester.get(url,
                    username, password);
            
            Document doc = Jsoup.parse(resPage);
            
            if (!this.isLogged(doc)) {
            	logger.log(Level.SEVERE, "isLogged is false!");
                return null; // login non riuscito

            } else {
            	
//                if(haveManyCareers(doc) == false){
                    UserInformation uInfo = this.parsingUserInfo(doc);

                    // Prende la matricola da un'altra pagina (non login)
                    uInfo.setStudentID(this.getStudentID(username, password, careerId));

                    return uInfo;
//                } else {
//                	String currentCareer;
//                	if (careerId == null)
//                		currentCareer = getCurrentCareer(doc);
//                	else {
//                		currentCareer = getCareerUrl(doc, careerId);
//                	}
//                    resPage = requester.get(new URL(currentCareer),
//                            username, password);
//                    doc = Jsoup.parse(resPage);
//                    UserInformation uInfo = this.parsingUserInfo(doc);
//                    // Prende la matricola da un'altra pagina (non login)
//                    uInfo.setStudentID(this.getStudentID(username, password, careerId));
//                    return uInfo;
//                }
            }

        } catch (MalformedURLException ex) {
        	logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

    }

    /**
     * Metodo privato che fa il parsing dell'html e restituisce un
     * UserInformation
     *
     * @param doc il Document da parsare
     * @return Le informazioni dell'utente
     */
    private UserInformation parsingUserInfo(Document doc) throws UnirestException {

        UserInformation uInfo = new UserInformation();

        // Getting Name And Surname
        Element name = doc.select("div#sottotitolo-menu-principale dt").first();
        if (name == null)
        	throw new UnirestException("No Student name found.");
        String[] nameAndSurname = name.html().split("\\u0026nbsp;");
        uInfo.setName(nameAndSurname[0]);
        if (nameAndSurname.length > 1)
        	uInfo.setSurname(nameAndSurname[1]);

        // Getting Class
        Element studentClass = doc.select(
                "#gu-textStatusStudenteCorsoFac-text-link2").first();
        Element studentCurrClass = doc.select("div#gu-boxStatusStudenteIscriz1 p.box-cfu-p b").get(1);
        uInfo.setStudentClass(studentClass.text() + " "
                + studentCurrClass.text() + " ANNO");

        // Getting additional information
        Elements addInform = doc.select("table[summary$=studente] td");
        /* scorre gli elementi td memorizzando il precedente, se il precedente
         * e' l'indicazione di una informazione utile allora vuol dire che
         * quello attuale e' l'informazione da recuperare
         */
        String precedente = "";
        for (Element tableCell : addInform) {
            if (precedente.equals("Tasse")) { // situazione tasse es: regolare
                String taxesUntrimmed = tableCell.text();
                String taxesTrimmed = StringUtils.realTrim(taxesUntrimmed);
                uInfo.setTaxes(taxesTrimmed);
            } else if (precedente.equals("Piano carriera")) {
                // se il piano carriera e' modificabile
                String careerPlanUntrimmed = tableCell.text();
                String careerPlanTrimmed = StringUtils.realTrim(careerPlanUntrimmed);
                uInfo.setCareerPlan(careerPlanTrimmed);
            } else if (precedente.equals("Appelli disponibili")) {
                // quanti appelli disponibili ci sono
                String avaibleExamsUntrimmed = tableCell.text();
                String avaibleExamsTrimmed = StringUtils.realTrim(avaibleExamsUntrimmed);
                String avaibleExams;
                if (avaibleExamsTrimmed.contains("appello disponibile")) {
                    avaibleExams = avaibleExamsTrimmed.replace(" appello disponibile", "");
                } else {
                    avaibleExams = avaibleExamsTrimmed.replace(" appelli disponibili", "");
                }
                int noAvaibleExams = Integer.parseInt(avaibleExams);
                uInfo.setAvailableExams(noAvaibleExams);
            } else if (precedente.equals("Iscrizioni appelli")) {
                // a quanti appelli lo studente e' iscritto
                String enrolledExamsUntrimmed = tableCell.text();
                String enrolledExamsTrimmed = StringUtils.realTrim(enrolledExamsUntrimmed);
                String enrolledExams;
                if (enrolledExamsTrimmed.contains("prenotazione")) {
                    enrolledExams = enrolledExamsTrimmed.replace(" prenotazione", "");
                } else {
                    enrolledExams = enrolledExamsTrimmed.replace(" prenotazioni", "");
                }                
                int noEnrolledExams = Integer.parseInt(enrolledExams);
                uInfo.setEnrolledExams(noEnrolledExams);
            }
            precedente = tableCell.text();
        }

        // getting course
        String course = doc.select("p[id=gu-textStatusStudenteCorsoFac").first().child(0).text();
        uInfo.setCourse(course);

        // getting department
        String department = doc.select("p[id=gu-textStatusStudenteCorsoFac").first().child(1).text();
        uInfo.setDepartment(department);

        // getting course path
        String coursePath = doc.select("p[id=gu-textStatusStudenteCorsoFac").first().child(2).text();
        uInfo.setCoursePath(coursePath);

        // getting course length
        String courseLengthUntrimmed = doc.select("p[class=box-cfu-p]").first().child(0).text();
        String courseLengthTrimmed = StringUtils.realTrim(courseLengthUntrimmed);
        String courseLengthNumber = courseLengthTrimmed.replace(" anni", "");
        int courseLength = Integer.parseInt(courseLengthNumber);
        uInfo.setCourseLength(courseLength);

        // getting registration date
        String registrationDate = doc.select("p[id=gu-textStatusStudenteImma").first().child(0).text();
        uInfo.setRegistrationDate(registrationDate);

        return uInfo;
    }

    /**
     * Metodo privato che controlla se il login ha avuto successo
     *
     * @param doc il Document da parsare
     * @return TRUE se il login ha avuto successo FALSE altrimenti
     */
    private boolean isLogged(Document doc) {
        Element firstMeta = doc.select("meta").first();

        /* quando il login non ha successo si ottiene una pagina con tag meta
         * per il redirect
         */
        if (firstMeta == null || firstMeta.attr("http-equiv").equals("refresh")) {
            // login non riuscito
            return false;
        }

        return true;
    }
    
    
    private boolean haveManyCareers(Document doc) {
        Element elementPageTitle = doc.select("div[class=titolopagina]").first();
        if (elementPageTitle != null) {
            String pageTitle = elementPageTitle.text();
            if (pageTitle != null) {
                if (pageTitle.equals("Scegli carriera")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private String getCurrentCareer(Document doc){
        String result = "";
        Element detailTable = doc.select("table[class=detail_table]").first();
        if(detailTable != null){
            Elements tdDetailTable = detailTable.select("td");
            if(tdDetailTable != null){
                String urlCareer = tdDetailTable.get(0).select("a").attr("abs:href");
                result = urlCareer;
            }
        }
        return result;
        
      //TODO: add error handling!
    }
    
    private String getCareerUrl(Document doc, String careerId) {
        String result = "";
        Element detailTable = doc.select("table[class=detail_table]").first();
        if(detailTable != null){
            Elements tdDetailTable = detailTable.select("td");
            if(tdDetailTable != null){
            	String urlCareer;
            	for (int i = 0; i < tdDetailTable.size(); i++) {
            		urlCareer = tdDetailTable.get(0).select("a").attr("abs:href");
            		if (urlCareer.contains(careerId))
            			result = urlCareer;
            	}
            }
        }
        return result;
        
        //TODO: add error handling!
    }
    
    /**
     * Recupera la matricola da un'altra pagina! dato che non e' presente nella
     * home di esse3
     *
     * @return lo studentID
     */
    private String getStudentID(String username, String password, String pCareerId)
            throws MalformedURLException, UnirestException {

        HTMLRequesterInterface req;
		try {
			req = HTMLRequesterManager.getManager().getInstance(username, password, pCareerId);
		} catch (HTMLRequesterException e) {
			throw new UnirestException(e.getMessage());
		}
        String resPage = req.get(new URL(ConfigurationManager.getInstance().getRecordBookUrl()),
                username, password);

        Document doc = Jsoup.parse(resPage);
        Element name = doc.select("div.titolopagina").first();

        /**
         * dato che la stringa ha questa forma: "Libretto di : IVAN DI RIENZO -
         * [MAT. 148432]" elimina la parentesi chiusa e infine splitta dividendo
         * da "MAT. " e prende la seconda stringa ottenuta cioe' la matricola
         */
        String ID = "???";
        if (name != null)
        	ID = name.text().replaceAll("]", "").split("MAT. ")[1];

        return ID;
    }

}
