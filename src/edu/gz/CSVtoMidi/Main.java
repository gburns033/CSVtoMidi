package edu.gz.CSVtoMidi;

import javax.sound.midi.*;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		try {
			List<MidiEventData> midiEvents = null;
			try {
				midiEvents = MidiCsvParser.parseCsv("./files/mysterysong.csv");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Sequence sequence = new Sequence(Sequence.PPQ, 384);
			Track track = sequence.createTrack();

			MidiEventFactoryAbstract factoryAbstract = new StandardMidiEventFactoryAbstract();
			// MidiEventFactoryAbstract factoryAbstract = new
			// LegatoMidiEventFactoryAbstract();
			// MidiEventFactoryAbstract factoryAbstract = new
			// StaccatoMidiEventFactoryAbstract();

			MidiEventFactory factory = factoryAbstract.createFactory();

			// Choose an instrument strategy (e.g., Trumpet, Bass Guitar, Piano)
			InstrumentStrategy instrumentStrategy = new ElectricBassGuitarStrategy();
			instrumentStrategy.applyInstrument(track, 0);

			instrumentStrategy = new TrumpetStrategy();
			instrumentStrategy.applyInstrument(track, 1);

			// Choose a pitch strategy (e.g., Higher Pitch, Lower Pitch)
			PitchStrategy pitchStrategy = new HigherPitchStrategy();

			for (MidiEventData event : midiEvents) {
				int modifiedNote = pitchStrategy.modifyPitch(event.getNote());
				// Modify pitch multiple times for a higher pitch
				modifiedNote = pitchStrategy.modifyPitch(modifiedNote);

				if (event.getNoteOnOff() == ShortMessage.NOTE_ON) {
					track.add(factory.createNoteOn(event.getStartEndTick(), modifiedNote, event.getVelocity(),
							event.getChannel()));
				} else {
					track.add(factory.createNoteOff(event.getStartEndTick(), modifiedNote, event.getChannel()));
				}
			}

			// Playing the sequence
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.start();

			while (sequencer.isRunning() || sequencer.isOpen()) {
				Thread.sleep(100);
			}

			Thread.sleep(500);
			sequencer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}