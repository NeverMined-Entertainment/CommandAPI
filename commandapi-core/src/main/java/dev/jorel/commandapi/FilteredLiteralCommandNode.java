package dev.jorel.commandapi;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

// Brigadier's CommandDispatcher#getCompletionSuggestions iterates parent.getChildren() and asks
// each child for suggestions without checking canUse(source). When a literal subcommand sits
// beside an argument node that has a server-side suggestion provider (e.g. EntitySelectorArgument),
// the client routes the tab-complete request through the server, and the unfiltered child loop
// leaks restricted literals to players without permission. This subclass guards listSuggestions
// with the node's own requires() predicate to close that gap.
final class FilteredLiteralCommandNode<S> extends LiteralCommandNode<S> {

	FilteredLiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement,
	                           CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
		super(literal, command, requirement, redirect, modifier, forks);
	}

	@Override
	public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (!canUse(context.getSource())) {
			return Suggestions.empty();
		}
		return super.listSuggestions(context, builder);
	}
}
