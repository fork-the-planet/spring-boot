/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.jarmode.tools;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Implicit {@code 'help'} command.
 *
 * @author Phillip Webb
 * @author Moritz Halbritter
 */
class HelpCommand extends Command {

	private final Context context;

	private final List<Command> commands;

	private final String jarMode;

	HelpCommand(Context context, List<Command> commands) {
		this(context, commands, System.getProperty("jarmode"));
	}

	HelpCommand(Context context, List<Command> commands, String jarMode) {
		super("help", "Help about any command", Options.none(), Parameters.of("[<command>]"));
		this.context = context;
		this.commands = commands;
		this.jarMode = (jarMode != null) ? jarMode : "tools";
	}

	@Override
	void run(PrintStream out, Map<Option, String> options, List<String> parameters) {
		run(out, parameters);
	}

	void run(PrintStream out, List<String> parameters) {
		String commandName = (parameters.isEmpty()) ? null : parameters.get(0);
		if (commandName == null) {
			printUsageAndCommands(out);
			return;
		}
		if (getName().equals(commandName)) {
			printCommandHelp(out, this, true);
			return;
		}
		Command command = Command.find(this.commands, commandName);
		if (command == null) {
			printError(out, "Unknown command \"%s\"".formatted(commandName));
			printUsageAndCommands(out);
			return;
		}
		printCommandHelp(out, command, true);
	}

	void printCommandHelp(PrintStream out, Command command, boolean printDeprecationWarning) {
		if (command.isDeprecated() && printDeprecationWarning) {
			printWarning(out, "This command is deprecated. " + command.getDeprecationMessage());
		}
		out.println(command.getDescription());
		out.println();
		out.println("Usage:");
		out.println("  " + getJavaCommand() + " " + getUsage(command));
		if (!command.getOptions().isEmpty()) {
			out.println();
			out.println("Options:");
			int maxNameLength = getMaxLength(0, command.getOptions().stream().map(Option::getNameAndValueDescription));
			command.getOptions().stream().forEach((option) -> printOptionSummary(out, option, maxNameLength));
		}
	}

	private void printOptionSummary(PrintStream out, Option option, int padding) {
		out.printf("  --%-" + padding + "s  %s%n", option.getNameAndValueDescription(), option.getDescription());
	}

	private String getUsage(Command command) {
		StringBuilder usage = new StringBuilder();
		usage.append(command.getName());
		if (!command.getOptions().isEmpty()) {
			usage.append(" [options]");
		}
		command.getParameters().getDescriptions().forEach((param) -> usage.append(" ").append(param));
		return usage.toString();
	}

	private void printUsageAndCommands(PrintStream out) {
		out.println("Usage:");
		out.println("  " + getJavaCommand());
		out.println();
		out.println("Available commands:");
		int maxNameLength = getMaxLength(getName().length(), this.commands.stream().map(Command::getName));
		this.commands.stream()
			.filter((command) -> !command.isDeprecated())
			.forEach((command) -> printCommandSummary(out, command, maxNameLength));
		printCommandSummary(out, this, maxNameLength);
		List<Command> deprecatedCommands = this.commands.stream().filter(Command::isDeprecated).toList();
		if (!deprecatedCommands.isEmpty()) {
			out.println("Deprecated commands:");
			for (Command command : deprecatedCommands) {
				printCommandSummary(out, command, maxNameLength);
			}
		}
	}

	private int getMaxLength(int minimum, Stream<String> strings) {
		return Math.max(minimum, strings.mapToInt(String::length).max().orElse(0));
	}

	private void printCommandSummary(PrintStream out, Command command, int padding) {
		out.printf("  %-" + padding + "s  %s%n", command.getName(), command.getDescription());
	}

	private String getJavaCommand() {
		return "java -Djarmode=" + this.jarMode + " -jar " + this.context.getArchiveFile().getName();
	}

	private void printError(PrintStream out, String errorMessage) {
		out.println("Error: " + errorMessage);
		out.println();
	}

	private void printWarning(PrintStream out, String errorMessage) {
		out.println("Warning: " + errorMessage);
		out.println();
	}

}
