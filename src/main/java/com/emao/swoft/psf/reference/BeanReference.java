package com.emao.swoft.psf.reference;

import com.emao.swoft.psf.BeanUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

public class BeanReference extends PsiPolyVariantReferenceBase<PsiElement> {

    final private PsiElement el;

    public BeanReference(@NotNull PsiElement psiElement) {
        super(psiElement);
        this.el = psiElement;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (BeanUtil.isPsfCallElement(this.el)) {
            return new ResolveResult[0];
        }
        PsiElement destEl = BeanUtil.getPsfCallElement(this.el);
        return new ResolveResult[] {
                new PsiElementResolveResult(destEl)
        };
    }

    @Override
    public @NotNull TextRange getAbsoluteRange() {
        return super.getAbsoluteRange();
    }

    @Override
    public Object @NotNull [] getVariants() {
        return super.getVariants();
    }
}
