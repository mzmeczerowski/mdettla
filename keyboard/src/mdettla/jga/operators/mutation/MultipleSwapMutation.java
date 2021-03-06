package mdettla.jga.operators.mutation;

import java.util.List;
import java.util.Random;

import mdettla.jga.core.MutationOperator;
import mdettla.jga.core.Specimen;
import mdettla.jga.core.Utils;

/**
 * Mutacja polegająca na zamianie pewnej ilości genów miejscami.
 */
public class MultipleSwapMutation implements MutationOperator {

	private Random random = new Random();

	@Override
	public void mutate(Specimen specimen) {
		int mutationsCount = (int)Math.floor(1 / ((random.nextDouble() + .1) * .9));
		mutationsCount = Math.min(mutationsCount, specimen.getGenotypeLength());
		List<Integer> mutationPositions = Utils.randomSample(
				Utils.range(specimen.getGenotypeLength()), mutationsCount * 2);
		for (int i = 0; i < mutationPositions.size(); i += 2) {
			Object tmp = specimen.getGeneAt(mutationPositions.get(i));
			specimen.setGeneAt(mutationPositions.get(i),
					specimen.getGeneAt(mutationPositions.get(i + 1)));
			specimen.setGeneAt(mutationPositions.get(i + 1), tmp);
		}
	}
}
