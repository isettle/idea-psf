package com.emao.swoft.psf.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import org.jetbrains.annotations.NotNull;

public class BeanReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
//        registrar.registerReferenceProvider(PlatformPatterns.psiElement(MethodReference.class),
//                new PsiReferenceProvider() {
//                    @Override
//                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
//                        return new PsiReference[]{new BeanReference(element)};
//                    }
//                }
//        );
    }
}
