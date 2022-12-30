package com.emao.swoft.psf.index;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.intellij.util.io.VoidDataExternalizer;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationStubIndex extends FileBasedIndexExtension<String, Void> {

    public static final ID<String, Void> KEY = ID.create("com.emao.swoft.psf");

    @Override
    public @NotNull ID<String, Void> getName() {
        return KEY;
    }

    @Override
    public @NotNull DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            final Map<String, Void> map = new THashMap<>();

            PsiFile psiFile = inputData.getPsiFile();
            if(!(psiFile instanceof PhpFile)) {
                return map;
            }

            psiFile.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if ((element instanceof PhpClass)) {
                        visitPhpClass((PhpClass) element);
                    }

                    super.visitElement(element);
                }

                private void visitPhpClass(PhpClass phpClass) {
                    PhpDocComment phpDocComment = phpClass.getDocComment();
                    if(phpDocComment == null) {
                        return;
                    }

                    PhpDocTag[] annotationDocTags = phpDocComment.getTagElementsByName("@Bean");
                    if (annotationDocTags.length <= 0) {
                        return;
                    }

                    String beanId = "";
                    for (PhpDocTag phpDocTag: annotationDocTags) {
                        String text = phpDocTag.getText();
                        String Regx = "\"([a-zA-Z.]+)\"";
                        if (text.contains("name")) {
                            Regx = "name=\"([a-zA-Z.]+)\"";
                        }
                        Matcher matcher = Pattern.compile(Regx).matcher(text);
                        while (matcher.find()) {
                            beanId = matcher.group(1);
                            map.put(beanId, null);
                        }
                    }
                }
            });
            return map;
        };
    }

    @Override
    public @NotNull KeyDescriptor<String> getKeyDescriptor() {
        return new EnumeratorStringDescriptor();
    }

    @Override
    public @NotNull DataExternalizer<Void> getValueExternalizer() {
        return VoidDataExternalizer.INSTANCE;
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public FileBasedIndex.@NotNull InputFilter getInputFilter() {
        return file -> {
            boolean con1 = file.getFileType() == PhpFileType.INSTANCE;
            boolean con2 = file.getPath().contains("EMao");
            boolean con3 = file.getPath().contains("Data");
            return con1 && con2 && con3;
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }
}
