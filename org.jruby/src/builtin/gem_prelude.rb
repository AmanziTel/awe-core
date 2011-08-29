# depends on: array.rb dir.rb env.rb file.rb hash.rb module.rb regexp.rb
# vim: filetype=ruby

# NOTICE: Ruby is during initialization here.
# * Encoding.default_external does not reflects -E.
# * Should not expect Encoding.default_internal.
# * Locale encoding is available.

# Note: We have disabled all of gem_prelude and just load rubygems here, since the
# logic below is terribly broken.

begin
  require 'rubygems'
rescue LoadError
  # For JRUBY-5333, gracefully fail to load, since stdlib may not be available
  warn 'rubygems.rb not found; disabling gems' if $VERBOSE
end

#if defined?(Gem) then
#
#  # :stopdoc:
#
#  module Kernel
#
#    def gem(gem_name, *version_requirements)
#      Gem.push_gem_version_on_load_path(gem_name, *version_requirements)
#    end
#    private :gem
#  end
#
#  module Gem
#
#    # FIXME: MRI hardcodes these at build time, but we have no choice but to load 'rbconfig'
#    require 'rbconfig'
#    ConfigMap = {
#      :EXEEXT            => RbConfig::CONFIG["EXEEXT"],
#      :RUBY_SO_NAME      => RbConfig::CONFIG["RUBY_SO_NAME"],
#      :arch              => RbConfig::CONFIG["arch"],
#      :bindir            => RbConfig::CONFIG["bindir"],
#      :libdir            => RbConfig::CONFIG["libdir"],
#      :ruby_install_name => RbConfig::CONFIG["ruby_install_name"],
#      :ruby_version      => RbConfig::CONFIG["ruby_version"],
#      :rubylibprefix     => RbConfig::CONFIG["rubylibprefix"],
#      :sitedir           => RbConfig::CONFIG["sitedir"],
#      :sitelibdir        => RbConfig::CONFIG["sitelibdir"],
#    }
#
#    def self.dir
#      @gem_home ||= nil
#      set_home(ENV['GEM_HOME'] || default_dir) unless @gem_home
#      @gem_home
#    end
#
#    def self.path
#      @gem_path ||= nil
#      unless @gem_path
#        paths = [ENV['GEM_PATH'] || default_path]
#        paths << APPLE_GEM_HOME if defined? APPLE_GEM_HOME
#        set_paths(paths.compact.join(File::PATH_SEPARATOR))
#      end
#      @gem_path
#    end
#
#    def self.post_install(&hook)
#      @post_install_hooks << hook
#    end
#
#    def self.post_uninstall(&hook)
#      @post_uninstall_hooks << hook
#    end
#
#    def self.pre_install(&hook)
#      @pre_install_hooks << hook
#    end
#
#    def self.pre_uninstall(&hook)
#      @pre_uninstall_hooks << hook
#    end
#
#    def self.set_home(home)
#      # FIXME: what is Encoding.find('filesystem') supposed to do?
#      #home = home.dup.force_encoding(Encoding.find('filesystem'))
#      home.gsub!(File::ALT_SEPARATOR, File::SEPARATOR) if File::ALT_SEPARATOR
#      @gem_home = home
#    end
#
#    def self.set_paths(gpaths)
#      if gpaths
#        @gem_path = gpaths.split(File::PATH_SEPARATOR)
#
#        if File::ALT_SEPARATOR then
#          @gem_path.map! do |path|
#            path.gsub File::ALT_SEPARATOR, File::SEPARATOR
#          end
#        end
#
#        @gem_path << Gem.dir
#      else
#        # TODO: should this be Gem.default_path instead?
#        @gem_path = [Gem.dir]
#      end
#
#      @gem_path.uniq!
#      # FIXME: what is Encoding.find('filesystem') supposed to do?
#      #@gem_path.map!{|x|x.force_encoding(Encoding.find('filesystem'))}
#      @gem_path.map!{|x|x}
#    end
#
#    def self.user_home
#      # FIXME: what is Encoding.find('filesystem') supposed to do?
#      #@user_home ||= File.expand_path("~").force_encoding(Encoding.find('filesystem'))
#      @user_home ||= File.expand_path("~").force_encoding(Encoding.find('filesystem'))
#    rescue
#      if File::ALT_SEPARATOR then
#        "C:/"
#      else
#        "/"
#      end
#    end
#
#    # begin rubygems/defaults
#    # NOTE: this require will be replaced with in-place eval before compilation.
#    # FIXME: this is hardcoded and needs to be replaced during the build
##module Gem
#
#  @post_install_hooks   ||= []
#  @post_uninstall_hooks ||= []
#  @pre_uninstall_hooks  ||= []
#  @pre_install_hooks    ||= []
#
#  ##
#  # An Array of the default sources that come with RubyGems
#
#  def self.default_sources
#    %w[http://gems.rubyforge.org/]
#  end
#
#  ##
#  # Default home directory path to be used if an alternate value is not
#  # specified in the environment
#
#  def self.default_dir
#    # FIXME: hardcoded to 1.8 gems dir for now
#    File.join(ConfigMap[:libdir], "ruby", "gems", "1.8")
#=begin
#    if defined? RUBY_FRAMEWORK_VERSION then
#      File.join File.dirname(ConfigMap[:sitedir]), 'Gems',
#                ConfigMap[:ruby_version]
#    # 1.9.2dev reverted to 1.8 style path
#    elsif RUBY_VERSION > '1.9' and RUBY_VERSION < '1.9.2' then
#      File.join(ConfigMap[:libdir], ConfigMap[:ruby_install_name], 'gems',
#                ConfigMap[:ruby_version])
#    else
#      File.join(ConfigMap[:libdir], ruby_engine, 'gems',
#                ConfigMap[:ruby_version])
#    end
#=end
#  end
#
#  ##
#  # Path for gems in the user's home directory
#
#  def self.user_dir
#    File.join(Gem.user_home, '.gem', ruby_engine,
#              ConfigMap[:ruby_version])
#  end
#
#  ##
#  # Default gem load path
#
#  def self.default_path
#    if File.exist?(Gem.user_home)
#      [user_dir, default_dir]
#    else
#      [default_dir]
#    end
#  end
#
#  ##
#  # Deduce Ruby's --program-prefix and --program-suffix from its install name
#
#  def self.default_exec_format
#    exec_format = ConfigMap[:ruby_install_name].sub('ruby', '%s') rescue '%s'
#
#    unless exec_format =~ /%s/ then
#      raise Gem::Exception,
#        "[BUG] invalid exec_format #{exec_format.inspect}, no %s"
#    end
#
#    exec_format
#  end
#
#  ##
#  # The default directory for binaries
#
#  def self.default_bindir
#    if defined? RUBY_FRAMEWORK_VERSION then # mac framework support
#      '/usr/bin'
#    else # generic install
#      ConfigMap[:bindir]
#    end
#  end
#
#  ##
#  # The default system-wide source info cache directory
#
#  def self.default_system_source_cache_dir
#    File.join Gem.dir, 'source_cache'
#  end
#
#  ##
#  # The default user-specific source info cache directory
#
#  def self.default_user_source_cache_dir
#    File.join Gem.user_home, '.gem', 'source_cache'
#  end
#
#  ##
#  # A wrapper around RUBY_ENGINE const that may not be defined
#
#  def self.ruby_engine
#    if defined? RUBY_ENGINE then
#      RUBY_ENGINE
#    else
#      'ruby'
#    end
#  end
#
##end
#    # end rubygems/defaults
#
#
#    ##
#    # Methods before this line will be removed when QuickLoader is replaced
#    # with the real RubyGems
#
#    GEM_PRELUDE_METHODS = Gem.methods(false)
#
#    begin
#      verbose, debug = $VERBOSE, $DEBUG
#      $VERBOSE = $DEBUG = nil
#
#      begin
#        require 'rubygems/defaults/operating_system'
#      rescue ::LoadError
#      end
#
#      if defined?(RUBY_ENGINE) then
#        begin
#          # FIXME: We can't do this right now because our defaults need full RubyGems
#          #require "rubygems/defaults/#{RUBY_ENGINE}"
#        rescue ::LoadError
#        end
#      end
#    ensure
#      $VERBOSE, $DEBUG = verbose, debug
#    end
#
#    module QuickLoader
#
#      @loaded_full_rubygems_library = false
#
#      def self.load_full_rubygems_library
#        return if @loaded_full_rubygems_library
#
#        @loaded_full_rubygems_library = true
#
#        class << Gem
#          Gem::GEM_PRELUDE_METHODS.each do |method_name|
#            undef_method method_name
#          end
#          undef_method :const_missing
#          undef_method :method_missing
#        end
#
#        Kernel.module_eval do
#          undef_method :gem if method_defined? :gem
#        end
#
#        # Case insenstive filesystems on any OS can have this issue, but
#        # rubygems may have been loaded with a slightly different case (e.g.
#        # C:rg/heh versus c:rg/heh).  Insensitive compare rules :(
#        if RbConfig::CONFIG["host_os"] == "mswin32"
#          $".reject! do |p|
#            p =~ /#{Regexp.escape path_to_full_rubygems_library}/i
#          end
#        else
#          $".reject! do |p|
#            p =~ /#{Regexp.escape path_to_full_rubygems_library}/
#          end
#        end
#
#        $".each do |path|
#          if /#{Regexp.escape File::SEPARATOR}rubygems\.rb\z/ =~ path
#            raise LoadError, "another rubygems is already loaded from #{path}"
#          end
#        end
#        require 'rubygems'
#      end
#
#      def self.fake_rubygems_as_loaded
#        path = path_to_full_rubygems_library
#        $" << path unless $".include?(path)
#      end
#
#      def self.path_to_full_rubygems_library
#        # FIXME: we use the rubygems in 1.8's site_ruby dir
#        #installed_path = File.join(Gem::ConfigMap[:rubylibprefix], Gem::ConfigMap[:ruby_version])
#        installed_path = File.join(Gem::ConfigMap[:rubylibprefix], "site_ruby", "1.8")
#        if $:.include?(installed_path)
#          return File.join(installed_path, 'rubygems.rb')
#        else # e.g., on test-all
#          $:.each do |dir|
#            if File.exist?( path = File.join(dir, 'rubygems.rb') )
#              return path
#            end
#          end
#          raise LoadError, 'rubygems.rb'
#        end
#      end
#
#      GemPaths = {}
#      GemVersions = {}
#
#      def push_gem_version_on_load_path(gem_name, *version_requirements)
#        if version_requirements.empty?
#          unless GemPaths.has_key?(gem_name) then
#            raise Gem::LoadError, "Could not find RubyGem #{gem_name} (>= 0)\n"
#          end
#
#          # highest version gems already active
#          return false
#        else
#          if version_requirements.length > 1 then
#            QuickLoader.load_full_rubygems_library
#            return gem(gem_name, *version_requirements)
#          end
#
#          requirement, version = version_requirements[0].split
#          requirement.strip!
#
#          if loaded_version = GemVersions[gem_name] then
#            case requirement
#            when ">", ">=" then
#              return false if
#                (loaded_version <=> Gem.integers_for(version)) >= 0
#            when "~>" then
#              required_version = Gem.integers_for version
#
#              return false if loaded_version.first == required_version.first
#            end
#          end
#
#          QuickLoader.load_full_rubygems_library
#          gem gem_name, *version_requirements
#        end
#      end
#
#      def integers_for(gem_version)
#        numbers = gem_version.split(".").collect {|n| n.to_i}
#        numbers.pop while numbers.last == 0
#        numbers << 0 if numbers.empty?
#        numbers
#      end
#
#      def push_all_highest_version_gems_on_load_path
#        Gem.path.each do |path|
#          gems_directory = File.join(path, "gems")
#
#          if File.exist?(gems_directory) then
#            Dir.entries(gems_directory).each do |gem_directory_name|
#              next if gem_directory_name == "." || gem_directory_name == ".."
#
#              next unless gem_name = gem_directory_name[/(.*)-(.*)/, 1]
#              new_version = integers_for($2)
#              current_version = GemVersions[gem_name]
#
#              if !current_version or (current_version <=> new_version) < 0 then
#                GemVersions[gem_name] = new_version
#                GemPaths[gem_name] = File.join(gems_directory, gem_directory_name)
#              end
#            end
#          end
#        end
#
#        require_paths = []
#
#        GemPaths.each_value do |path|
#          if File.exist?(file = File.join(path, ".require_paths")) then
#            paths = File.read(file).split.map do |require_path|
#              File.join path, require_path
#            end
#
#            require_paths.concat paths
#          else
#            require_paths << file if File.exist?(file = File.join(path, "bin"))
#            require_paths << file if File.exist?(file = File.join(path, "lib"))
#          end
#        end
#
#        # "tag" the first require_path inserted into the $LOAD_PATH to enable
#        # indexing correctly with rubygems proper when it inserts an explicitly
#        # gem version
#        unless require_paths.empty? then
#          require_paths.first.instance_variable_set(:@gem_prelude_index, true)
#        end
#        # gem directories must come after -I and ENV['RUBYLIB']
#        # FIXME: just adding to end
#        #$:[$:.index(ConfigMap[:sitelibdir]),0] = require_paths
#        $:.concat require_paths
#      end
#
#      def const_missing(constant)
#        QuickLoader.load_full_rubygems_library
#
#        if Gem.const_defined?(constant) then
#          Gem.const_get constant
#        else
#          super
#        end
#      end
#
#      def method_missing(method, *args, &block)
#        QuickLoader.load_full_rubygems_library
#        super unless Gem.respond_to?(method)
#        Gem.send(method, *args, &block)
#      end
#    end
#
#    extend QuickLoader
#
#  end
#
#  begin
#    Gem.push_all_highest_version_gems_on_load_path
#    Gem::QuickLoader.fake_rubygems_as_loaded
#  rescue Exception => e
#    puts "Error loading gem paths on load path in gem_prelude"
#    puts e
#    puts e.backtrace.join("\n")
#  end
#
#end
#
