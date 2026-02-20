package com.cotizia.cotizia.services;

import com.cotizia.cotizia.models.Cycle;
import com.cotizia.cotizia.models.Participant;
import com.cotizia.cotizia.models.Echeance;
import com.cotizia.cotizia.implementation.ParticipantDAO;
import com.cotizia.cotizia.implementation.EcheanceDAO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;
import java.util.List;

public class ReportService {

    private ParticipantDAO participantDAO = new ParticipantDAO();
    private EcheanceDAO echeanceDAO = new EcheanceDAO();

    public void generateCycleReport(Cycle cycle, File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Rapport de Cycle : " + cycle.getNom(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Info Cycle
        document.add(new Paragraph("Montant Cotisation : " + cycle.getMontantCotisation() + " FG"));
        document.add(new Paragraph("Fréquence : " + cycle.getFrequence()));
        document.add(new Paragraph("Date début : " + cycle.getDateDebut()));
        document.add(new Paragraph(
                "Collecteur : " + cycle.getCollecteur().getNom() + " " + cycle.getCollecteur().getPrenom()));
        document.add(new Paragraph(" "));

        // Participants Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(new PdfPCell(new Phrase("Participant")));
        table.addCell(new PdfPCell(new Phrase("Position")));
        table.addCell(new PdfPCell(new Phrase("Payé")));
        table.addCell(new PdfPCell(new Phrase("Statut")));

        List<Participant> participants = (List<Participant>) (List<?>) participantDAO.findByCycle(cycle.getId());
        for (Participant p : participants) {
            table.addCell(p.getUtilisateur().getNom() + " " + p.getUtilisateur().getPrenom());
            table.addCell(String.valueOf(p.getPositionBeneficiaire()));

            List<Echeance> echeances = echeanceDAO.findByParticipant(p.getId());
            double totalPaid = 0;
            boolean allPaid = true;
            for (Echeance e : echeances) {
                totalPaid += e.getMontantPaye();
                if (!"PAYE".equals(e.getStatut()))
                    allPaid = false;
            }
            table.addCell(String.valueOf(totalPaid) + " FG");
            table.addCell(allPaid ? "A JOUR" : "RETARD/ATTENTE");
        }

        document.add(table);
        document.close();
    }
}
