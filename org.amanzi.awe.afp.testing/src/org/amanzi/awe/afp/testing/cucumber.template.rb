# requires that @plugin_lib_dir and @plugin_features_dir be substituted with the correct values
require 'rubygems'

gem 'cucumber'
require 'cucumber/cli/main'
gem 'rspec'
require 'spec'


# override Kernel.exit to not do anything, this is to prevent evaler to 'throw' up when cucumber calls exit
module Kernel
	class << self
	puts "template classpath: ", $CLASSPATH
    def exit(*argv)
		p 'called Kernel.exit, not exiting.'
		end
	end
end

$:.unshift "@plugin_lib_dir"

Cucumber::Cli::Main.execute(['--format', 'pretty', "@plugin_features_dir"])

