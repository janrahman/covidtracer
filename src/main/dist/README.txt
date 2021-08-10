=== Covid Tracer ===
Entwickler: Jan Rahman (jan.rahman@hhu.de)
Anforderungen: Java 8, Windows 10, Unix
Getestet auf: Kubuntu 20.04, Windows 10
Version: 0.1


== Beschreibung ==
Die Webanwendung speichert Personen und Mitarbeiter,
welche dem SARS-CoV-2-Virus ausgesetzt wurden.
Insbesondere sollen tatsächliche Infektionen und Quarantäneaufenthalte nachverfolgt werden.

Da diese Personen an das Gesundheitsamt weitergeleitet werden müssen, bietet die Anwendung an, die Daten in ein geeignetes Excel-Format zu exportieren, um den Kommunikationsprozess zwischen Krankenhaus und Gesundheitsamt zu verbessern.

Des Weiteren können die Daten zur Auswertung verarbeitet werden und eine bessere Übersicht darstellen.

== Voraussetzungen ==

Es wird eine lokale Java-Installation in mindestens Version 8 benötigt.

Um zu prüfen ob und welches Java vorhanden ist gehen Sie folgendermaßen vor:

- Öffnen Sie eine Eingabeaufforderung (Windows-R Taste und dann in dem Feld cmd eingeben)
- In der Eingabeaufforderung können Sie mit: java -version herausfinden, welche Java Version installiert ist.

Eine Installationsanleitung für Java finden Sie unter: https://www.java.com/de/download/help/download_options.html

== Installation

Nach Download und Entpacken der Dateien starten Sie die Datei covidtracer.bat im Ordner bin

Danach öffnen Sie Ihren Browser und besuchen die Seite: http:localhost:8080