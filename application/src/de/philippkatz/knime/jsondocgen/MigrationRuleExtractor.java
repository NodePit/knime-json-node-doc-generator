package de.philippkatz.knime.jsondocgen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.workflow.migration.MigrationNodeMatchResult;
import org.knime.workflow.migration.NodeMigrationRuleRegistry;
import org.knime.workflow.migration.model.MigrationNode;
import org.mockito.Mockito;

import de.philippkatz.knime.jsondocgen.docs.MigrationRuleDoc;
import de.philippkatz.knime.jsondocgen.docs.MigrationRuleDoc.MigrationRuleDocBuilder;

/**
 * Extract migration rules - their migration “framework” is the probably most
 * complex thinkable solution from a technical and API level, yet frighteningly
 * limited at the end.
 * 
 * This code tries to extract an original node factory ID and its corresponding
 * replacement factory.
 * 
 * Context-specific properties, where the migration depends e.g. on settings are
 * _not_ covered - however only few rules actually make use of them.
 * 
 * @author Philipp Katz
 */
public class MigrationRuleExtractor {

	private static final Logger LOGGER = Logger.getLogger(MigrationRuleExtractor.class);

	public static List<MigrationRuleDoc> extractMigrationRules() {

		var migrationRules = NodeMigrationRuleRegistry.getInstance().getRules();
		// they replaced this in 5.2 but of course the “get all” is not accessible -
		// facepalm; in case it gets removed, we'll need to build this ourselves or get
		// it from the “generate node documenation” phase instead
		var nodeFactoryExtensions = NodeFactoryExtensionManager.getInstance().getNodeFactoryExtensions();

		LOGGER.info(String.format("Generating %s migration rules", migrationRules.size()));

		var migrationRuleInfos = new ArrayList<MigrationRuleDoc>();
		for (var rule : migrationRules) {
			try {
				// (1) match
				var matchMethod = getDeclaredMethodSuper(rule.getClass(), "match", MigrationNode.class);
				matchMethod.setAccessible(true);

				var getReplacementNFClass = getDeclaredMethodSuper(rule.getClass(), "getReplacementNodeFactoryClass",
						MigrationNode.class, MigrationNodeMatchResult.class);
				getReplacementNFClass.setAccessible(true);

				// we'll need to loop through all known nodes here to extract the rules -
				// probably it makes sense to integrate this into the node documentation loop,
				// as we have |node| >> |migration_rule|
				for (var nodeFactoryExtension : nodeFactoryExtensions) {
					var migrationNodeMock = Mockito.mock(MigrationNode.class);
					var factoryClass = nodeFactoryExtension.getFactory().getClass();
					Mockito.when(migrationNodeMock.getOriginalNodeFactoryClass()).then(invocation -> factoryClass);
					Mockito.when(migrationNodeMock.getOriginalNodeFactoryClassName())
							.then(invocation -> factoryClass.getName());

					var matchResult = (MigrationNodeMatchResult) matchMethod.invoke(rule,
							new Object[] { migrationNodeMock });
					var nodeActions = matchResult.getNodeActions();
					if (nodeActions.size() == 1) {
						LOGGER.info(String.format("Rule %s returned %s actions for original %s",
								rule.getClass().getName(), nodeActions.size(), factoryClass.getName()));

						// (2) replacement
						@SuppressWarnings("unchecked")
						var replacementFactoryClass = (Class<? extends NodeFactory<?>>) getReplacementNFClass
								.invoke(rule, new Object[] { migrationNodeMock, matchResult });

						migrationRuleInfos.add(new MigrationRuleDocBuilder() //
								.setOriginalNodeFactoryClass(factoryClass.getName()) //
								.setReplacementNodeFactoryClass(replacementFactoryClass.getName()) //
								.build()); //
					} else if (nodeActions.size() > 1) {
						LOGGER.error(
								String.format("Rule %s returned %s actions for %s - this is unexpected and unsupported",
										rule.getClass().getName(), factoryClass.getName(), nodeActions.size()));
					}
				}

			} catch (Exception e) {
				Throwable unwrapped = e;
				if (e instanceof InvocationTargetException ite) {
					unwrapped = ite.getTargetException();
				}
				LOGGER.warn(String.format("Error for %s: %s", rule.getClass().getName(), unwrapped.getMessage()),
						unwrapped);
			}
		}

		return migrationRuleInfos;

	}

	/**
	 * Get a declared method on the class, or any super class.
	 * 
	 * @param cl
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 * @throws IllegalStateException If there is no such method.
	 */
	private static final Method getDeclaredMethodSuper(Class<?> cl, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		for (;;) {
			try {
				method = cl.getDeclaredMethod(methodName, parameterTypes);
				break;

			} catch (NoSuchMethodException e) {
				var superClass = cl.getSuperclass();
				if (superClass == null) {
					break;
				}
				cl = superClass;
			}
		}
		if (method == null) {
			throw new IllegalStateException(
					String.format("method %s is not implemented by class or super classes", methodName));
		}

		return method;
	}

	private MigrationRuleExtractor() {
		// ...
	}

}
