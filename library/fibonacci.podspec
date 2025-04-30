Pod::Spec.new do |spec|
    spec.name                     = 'fibonacci'
    spec.version                  = '1.0.9'
    spec.homepage                 = 'https://github.com/gironnetd/fibonacci'
    spec.source                   = { :git => 'git@github.com:gironnetd/fibonacci.git', :tag => '1.0.9' }
    spec.vendored_frameworks      = 'library/build/cocoapods/framework/#{spec.name}.framework'
    spec.authors                  = 'Damien Gironnet'
    spec.license                  = { :type => 'MIT', :text => 'License text'}
    spec.summary                  = 'Some description for a Kotlin/Native module'
    spec.vendored_frameworks      = 'build/cocoapods/framework/fibonacci.framework'
    spec.libraries                = 'c++'
                
                
                
    if !Dir.exist?('build/cocoapods/framework/fibonacci.framework') || Dir.empty?('build/cocoapods/framework/fibonacci.framework')
        raise "

        Kotlin framework 'fibonacci' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :library:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':library',
        'PRODUCT_MODULE_NAME' => 'fibonacci',
    }
                
    spec.script_phases = [
        {
            :name => 'Build fibonacci',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end