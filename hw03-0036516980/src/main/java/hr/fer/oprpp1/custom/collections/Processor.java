package hr.fer.oprpp1.custom.collections;

/**
 * Predstavlja idejni ugovor između korisnika koji predaje objekt nad kojim se
 * treba obaviti operacija i svakog konkretnog <code>Proccesor-a</code> koji zna
 * kako obaviti određenu operaciju
 * 
 * @author vedran
 *
 */
public interface Processor {
	/**
	 * Obavlja operaciju nad predanim objektom.
	 * 
	 * @param value objekt nad kojim se obavlja operacija
	 */
	public void process(Object value);
}
