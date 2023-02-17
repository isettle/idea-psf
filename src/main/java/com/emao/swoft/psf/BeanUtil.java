package com.emao.swoft.psf;

import com.emao.swoft.psf.index.AnnotationStubIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BeanUtil {

    public static Boolean isPsfCallElement(@NotNull PsiElement el) {
        // 筛选psf方法
        boolean con1 = el instanceof MethodReference;
        boolean con2 = el.toString().contains("psf()");
        if (!con1 || !con2) {
            return false;
        }

        // 跳转
        MethodReference m = (MethodReference)el;
        PsiElement @NotNull [] methodParams = m.getParameters();
        if (methodParams.length < 1) {
            return false;
        }

        return true;
    }

    public static PsiElement getPsfCallElement(@NotNull PsiElement el) {
        // 跳转
        MethodReference m = (MethodReference)el;
        PsiElement @NotNull [] methodParams = m.getParameters();

        String beanInfo = methodParams[0].getText().replaceAll("'", "").replaceAll("\"", "");
        String[] beanVals = beanInfo.split("::");

        @NotNull Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(AnnotationStubIndex.KEY, beanVals[0], GlobalSearchScope.allScope(el.getProject()));
        final PsiElement[] destEl = {el};
        files.forEach(file->{
            PsiFile psiFile = PsiManager.getInstance(el.getProject()).findFile(file);
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

        return destEl[0];
    }
}
