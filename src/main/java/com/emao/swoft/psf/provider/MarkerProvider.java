package com.emao.swoft.psf.provider;

import com.emao.swoft.psf.index.AnnotationStubIndex;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Function;
import com.intellij.util.PsiNavigateUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public class MarkerProvider implements LineMarkerProvider {
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        // 筛选psf方法
        boolean con1 = element instanceof MethodReference;
        boolean con2 = element.toString().contains("psf()");
        if (!con1 || !con2) {
            return null;
        }

        // 跳转
        MethodReference m = (MethodReference)element;
        PsiElement @NotNull [] methodParams = m.getParameters();
        if (methodParams.length < 1) {
            return null;
        }
        String beanInfo = methodParams[0].getText().replaceAll("'", "").replaceAll("\"", "");
        String[] beanVals = beanInfo.split("::");

        @NotNull Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(AnnotationStubIndex.KEY, beanVals[0], GlobalSearchScope.allScope(element.getProject()));
        final PsiElement[] destEl = {element};
        files.forEach(file->{
            PsiFile psiFile = PsiManager.getInstance(element.getProject()).findFile(file);
            psiFile.accept(new PsiRecursiveElementWalkingVisitor(){
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if ((element instanceof Method)) {
                        visitPhpMethod((Method) element);
                    }

                    super.visitElement(element);
                }

                private void visitPhpMethod(Method method) {
                    String methodName = method.getName();
                    if (methodName.equals(beanVals[1])) {
                        destEl[0] = method;
                    }
                }
            });
        });

        return new LineMarkerInfo<>(
                element,
                element.getTextRange(),
                AllIcons.General.ArrowRight,
                (Function) o -> "psf",
                (e, elt) -> {
                    PsiNavigateUtil.navigate(destEl[0]);
                },
                GutterIconRenderer.Alignment.LEFT,
                (Supplier<String>) () -> "psf"
        );
    }
}
