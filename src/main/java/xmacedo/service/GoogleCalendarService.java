package xmacedo.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import xmacedo.model.Reuniao;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Integrate-Google-Calendar";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CLIENT_SECRET_DIR = "/client_secret.json";
    private static final String FOLDER_TMP = "/tmp/";
    private static final String KEY_NAME = "key_googleCalendar";
    private static final String LOCAL_AND_NAME_P12 = "/tmp/chave.p12";
    private static final String GOOGLE_USER_ID = "seu-id-google-identificacao";
    private static final String GOOGLE_CALENDAR = "chave-da-sua-genda-no-calendar";
    private static com.google.api.services.calendar.Calendar client;
    private static HttpTransport httpTransport;
    private static Credential credential;

    private static Credential authorizeByFlow() throws Exception{
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleCalendarService.class.getResourceAsStream(CLIENT_SECRET_DIR)));

        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(FOLDER_TMP));

        DataStore<StoredCredential> dataStore = dataStoreFactory.getDataStore(KEY_NAME);
        GoogleAuthorizationCodeFlow flowb = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR))
                .setAccessType("offline")
                .setCredentialDataStore(dataStore).build();

        return new AuthorizationCodeInstalledApp(flowb, new LocalServerReceiver()).authorize(GOOGLE_USER_ID);

    }

    private static Credential authorizeBykeyP12() throws Exception{

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(GOOGLE_USER_ID)
                .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
                .setServiceAccountPrivateKeyFromP12File(new File(LOCAL_AND_NAME_P12))
                .build();

        return credential;
    }

    public void adicionarReuniao(Reuniao reuniao) {
        try {

            inicializaVariaveis();

            Event event = criarEvento(reuniao);

            event = client.events()
                    .insert(GOOGLE_CALENDAR, event)
                    .setSendNotifications(true)//habilitar notificacao de e-mail do convite
                    .execute();
            System.out.printf("Event created: %s\n", event.getHtmlLink());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializaVariaveis() {
        try {

            if (httpTransport == null) {
                credential = authorizeBykeyP12();
            }
            credential = authorizeByFlow();

            // set up global Calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Event criarEvento(Reuniao reuniao) {

        Event event = new Event()
                .setSummary("Reuniao com: " +reuniao.getNome())
                .setDescription(reuniao.getDescricao())
                .setLocation(reuniao.getLocal())
                .setCreator(new Event.Creator().setEmail("seuemail@seidominio.com.br"));

        DateTime startDateTime = new DateTime(reuniao.getDataInicio());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setStart(start);

        Date dataFim = DateUtils.addMinutes(reuniao.getDataInicio(),reuniao.getDuracao());

        DateTime endDateTime = new DateTime(dataFim);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setEnd(end);

        event.setHangoutLink(null);
        EventAttendee[] attendees;

        attendees = new EventAttendee[]{
                new EventAttendee().setEmail("seuemail@seidominio.com.br").setOrganizer(true)
        };

        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        return event;
    }
}