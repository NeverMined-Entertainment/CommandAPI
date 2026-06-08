package dev.jorel.commandapi;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

// Mirrors LiteralArgumentBuilder but builds a FilteredLiteralCommandNode instead of the default,
// so every literal CommandAPI registers gets canUse-aware suggestion filtering.
final class FilteredLiteralArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

	private FilteredLiteralArgumentBuilder(String literal) {
		super(literal);
	}

	public static <S> FilteredLiteralArgumentBuilder<S> literal(String name) {
		return new FilteredLiteralArgumentBuilder<>(name);
	}

	@Override
	public LiteralCommandNode<S> build() {
		LiteralCommandNode<S> result = new FilteredLiteralCommandNode<>(
			getLiteral(),
			getCommand(),
			getRequirement(),
			getRedirect(),
			getRedirectModifier(),
			isFork()
		);
		for (CommandNode<S> child : getArguments()) {
			result.addChild(child);
		}
		return result;
	}
}
