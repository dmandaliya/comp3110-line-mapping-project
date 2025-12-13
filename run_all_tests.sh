#!/bin/bash
set -e

mkdir -p eclipseTestResults

run_test () {
  NAME=$1
  OLD=$2
  NEW=$3
  XML=$4

  echo "=============================="
  echo "Running test: $NAME"
  echo "=============================="
  echo "  Running LineMappingTool..."
  java -cp src LineMappingTool "$OLD" "$NEW" "eclipseTestResults/$NAME.map"
  echo "  Evaluating with Evaluator..."
  cat "eclipseTestResults/$NAME.map" | java -cp src Evaluator "$XML"
}

# Professor Eclipse dataset (eclipseTest/)
run_test ArrayReference eclipseTest/ArrayReference_1.java eclipseTest/ArrayReference_2.java eclipseTest/ArrayReference.xml
run_test asdf eclipseTest/asdf_1.java eclipseTest/asdf_2.java eclipseTest/asdf.xml
run_test ASTResolving eclipseTest/ASTResolving_1.java eclipseTest/ASTResolving_2.java eclipseTest/ASTResolving.xml
run_test BaseTypes eclipseTest/BaseTypes_1.java eclipseTest/BaseTypes_2.java eclipseTest/BaseTypes.xml
run_test BuildPathsPropertyPage eclipseTest/BuildPathsPropertyPage_1.java eclipseTest/BuildPathsPropertyPage_2.java eclipseTest/BuildPathsPropertyPage.xml
run_test CompilationUnitDocumentProvider eclipseTest/CompilationUnitDocumentProvider_1.java eclipseTest/CompilationUnitDocumentProvider_2.java eclipseTest/CompilationUnitDocumentProvider.xml
run_test CPListLabelProvider eclipseTest/CPListLabelProvider_1.java eclipseTest/CPListLabelProvider_2.java eclipseTest/CPListLabelProvider.xml
run_test DeltaProcessor eclipseTest/DeltaProcessor_1.java eclipseTest/DeltaProcessor_2.java eclipseTest/DeltaProcessor.xml
run_test DialogCustomize eclipseTest/DialogCustomize_1.java eclipseTest/DialogCustomize_2.java eclipseTest/DialogCustomize.xml
run_test DirectoryDialog eclipseTest/DirectoryDialog_1.java eclipseTest/DirectoryDialog_2.java eclipseTest/DirectoryDialog.xml
run_test DoubleCache eclipseTest/DoubleCache_1.java eclipseTest/DoubleCache_2.java eclipseTest/DoubleCache.xml
run_test FontData eclipseTest/FontData_1.java eclipseTest/FontData_2.java eclipseTest/FontData.xml
run_test GC eclipseTest/GC_1.java eclipseTest/GC_2.java eclipseTest/GC.xml
run_test GC2 eclipseTest/GC2_1.java eclipseTest/GC2_2.java eclipseTest/GC2.xml
run_test JavaCodeScanner eclipseTest/JavaCodeScanner_1.java eclipseTest/JavaCodeScanner_2.java eclipseTest/JavaCodeScanner.xml
run_test JavaModelManager eclipseTest/JavaModelManager_1.java eclipseTest/JavaModelManager_2.java eclipseTest/JavaModelManager.xml
run_test JavaPerspectiveFactory eclipseTest/JavaPerspectiveFactory_1.java eclipseTest/JavaPerspectiveFactory_2.java eclipseTest/JavaPerspectiveFactory.xml
run_test PluginSearchScope eclipseTest/PluginSearchScope_1.java eclipseTest/PluginSearchScope_2.java eclipseTest/PluginSearchScope.xml
run_test RefreshLocal eclipseTest/RefreshLocal_1.java eclipseTest/RefreshLocal_2.java eclipseTest/RefreshLocal.xml
run_test ResourceCompareInput eclipseTest/ResourceCompareInput_1.java eclipseTest/ResourceCompareInput_2.java eclipseTest/ResourceCompareInput.xml
run_test ResourceInfo eclipseTest/ResourceInfo_1.java eclipseTest/ResourceInfo_2.java eclipseTest/ResourceInfo.xml
run_test SaveManager eclipseTest/SaveManager_1.java eclipseTest/SaveManager_2.java eclipseTest/SaveManager.xml
run_test TabFolder eclipseTest/TabFolder_1.java eclipseTest/TabFolder_2.java eclipseTest/TabFolder.xml

echo "=============================="
echo "PROFESSOR TESTS COMPLETED"
echo "=============================="
