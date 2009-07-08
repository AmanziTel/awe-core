RUBY_SCRIPT_NAME = "Ruby script.rb"
RUBY_PROJECT_NAME = "Ruby Project"
AWE_PROJECT_NAME = "Awe Project"
ANOTHER_RUBY_PROJECT_NAME = "Another Ruby Project"

class TestRDT < Test::Unit::TestCase

  def setup
  	workspacePath = Java::org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot().getLocation()
  	@aweProject = Java::net.refractions.udig.project.internal.ProjectPlugin.getPlugin.getProjectRegistry.getProject(AWE_PROJECT_NAME)
  	
  	@iProject = Java::org.eclipse.core.resources.ResourcesPlugin.getWorkspace.getRoot.getProject(RUBY_PROJECT_NAME)
	@iProject.create(nil)
	@iProject.open(nil)  	
  		
  	@rubyProject = Java::org.rubypeople.rdt.core.RubyCore.create(@iProject, @aweProject.getName)  	  	
  	@rubyProject.getOpenable.open(nil)
  	
  	Java::org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathsBlock.addRubyNature(@iProject, nil)
  end
  
  def teardown
  	Java::org.eclipse.core.resources.ResourcesPlugin.getWorkspace.getRoot.delete(true, nil)
  	Java::net.refractions.udig.project.internal.ProjectPlugin.getPlugin.getProjectRegistry.getProjects.remove(@aweProject)  	
  end

  def test01_create_Ruby_Script
    puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test01_create_Ruby_Script"
    
    rubyScriptsBefore = @aweProject.getElements.get(0).getRubyElementsInternal.size
    
    iFile = @iProject.getFile(RUBY_SCRIPT_NAME)
    iFile.create(Java::java.io.ByteArrayInputStream.new(Java::java.lang.String.new("Hallo").getBytes), true, nil)
    sourceFolder = @rubyProject.getSourceFolderRoot(iFile.getParent)
    sourceFolder.open(nil)
    
    script = Java::org.rubypeople.rdt.internal.core.RubyModelManager.createRubyScriptFrom(iFile, @rubyProject)
    
    rubyScriptsAfter = @aweProject.getElements.get(0).getRubyElementsInternal.size
    
    assert_equal rubyScriptsBefore + 1, rubyScriptsAfter
    
    rubyScript = @aweProject.getElements.get(0).getRubyElementsInternal.get(0)
    
    assert_equal RUBY_SCRIPT_NAME, rubyScript.getName
    assert_equal script.getElementName, rubyScript.getName
  end
  
  def test02_create_Ruby_Project
  	puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test02_create_Ruby_Project"
  
  	rubyProjectBefore = @aweProject.getElements.size
  	
  	anotherProject = Java::org.eclipse.core.resources.ResourcesPlugin.getWorkspace.getRoot.getProject(ANOTHER_RUBY_PROJECT_NAME)
	anotherProject.create(nil)
	anotherProject.open(nil)  	
  		
  	anotherRubyProject = Java::org.rubypeople.rdt.core.RubyCore.create(anotherProject, @aweProject.getName)  	  	
  	anotherRubyProject.getOpenable.open(nil)
  	
  	rubyProjectAfter = @aweProject.getElements.size
  	
  	assert_equal rubyProjectBefore + 1, rubyProjectAfter

	rubyProject = @aweProject.getElements.get(1)
  	
  	assert_equal ANOTHER_RUBY_PROJECT_NAME, rubyProject.getName
  	assert_equal anotherRubyProject.getElementName, rubyProject.getName  	
  end
  
  def test03_launch_Ruby_Script
  	puts "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
    puts "test03_launch_Ruby_Script"
    
    iFile = @iProject.getFile(RUBY_SCRIPT_NAME)
    iFile.create(Java::java.io.ByteArrayInputStream.new(Java::java.lang.String.new("Hallo").getBytes), true, nil)
    sourceFolder = @rubyProject.getSourceFolderRoot(iFile.getParent)
    sourceFolder.open(nil)
    
    assert_nothing_raised do
    	config = Java::org.eclipse.debug.core.DebugPlugin.getDefault.getLaunchManager.getLaunchConfiguration(iFile)
    	Java::org.eclipse.debug.ui.DebugUITools.launch(config, "run")    	
    end  
    
  end

end