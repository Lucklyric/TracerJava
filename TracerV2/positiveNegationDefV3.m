%TP FP TN FN Defnintion
function [sensitivity,precision,fpr] = positiveNegationDefV3(pointsB,pointsC,arrayAB,arrayAC,arrayCB,arrayBC)
    %getABset
    positiveAB = [];
    negativeAB = [];
    positiveAC = [];
    negativeAC = [];
    positiveCB = [];
    negativeCB = [];
    positiveBC = [];
    negativeBC = [];
    
    for i = 1 : numel(arrayAB)
        if arrayAB(i) <= 3
            positiveAB(end+1,:) = pointsB(i,:);
        else
            negativeAB(end+1,:) = pointsB(i,:);
        end
    end
    
    for i = 1 : numel(arrayAC)
        if arrayAC(i) <= 3
            positiveAC(end+1,:) = pointsC(i,:);
        else
            negativeAC(end+1,:) = pointsC(i,:);
        end
    end
    
    for i = 1 : numel(arrayCB)
        if arrayCB(i) <= 3
            positiveCB(end+1,:) = pointsB(i,:);
        else
            negativeCB(end+1,:) = pointsB(i,:);
        end
    end
    
    for i = 1 : numel(arrayBC)
        if arrayBC(i) <= 3
            positiveBC(end+1,:) = pointsC(i,:);
        else
            negativeBC(end+1,:) = pointsC(i,:);
        end
    end
    
    positiveAB = unique(positiveAB,'rows');
    positiveAC = unique(positiveAC,'rows');
    negativeAB = unique(negativeAB,'rows');
    negativeAC = unique(negativeAC,'rows');
    
    positiveBC = unique(positiveBC,'rows');
    positiveCB = unique(positiveCB,'rows');
    negativeBC = unique(negativeBC,'rows');
    negativeCB = unique(negativeCB,'rows');
    
    realPositive = [positiveBC;positiveCB];
    realPositive = unique(realPositive,'rows');
    
    positiveBC = [];
    negativeBC = [];
    
    for i = 1 : numel(arrayAB)
        if arrayAB(i) <= 3
            positiveAB(end+1,:) = pointsB(i,:);
        else
            negativeAB(end+1,:) = pointsB(i,:);
        end
    end
    
    for i = 1 : numel(arrayAC)
        if arrayAC(i) <= 3
            positiveAC(end+1,:) = pointsC(i,:);
        else
            negativeAC(end+1,:) = pointsC(i,:);
        end
    end
    
    for i = 1 : numel(arrayCB)
        if arrayCB(i) <= 3
            positiveCB(end+1,:) = pointsB(i,:);
        else
            negativeCB(end+1,:) = pointsB(i,:);
        end
    end
    
    for i = 1 : numel(arrayBC)
        if arrayBC(i) <= 3
            positiveBC(end+1,:) = pointsC(i,:);
        else
            negativeBC(end+1,:) = pointsC(i,:);
        end
    end
    
    positiveAB = unique(positiveAB,'rows');
    positiveAC = unique(positiveAC,'rows');
    negativeAB = unique(negativeAB,'rows');
    negativeAC = unique(negativeAC,'rows');
    
    positiveBC = unique(positiveBC,'rows');
    positiveCB = unique(positiveCB,'rows');
    negativeBC = unique(negativeBC,'rows');
    negativeCB = unique(negativeCB,'rows');
    
    realPositive = [positiveBC;positiveCB];
    realPositive = unique(realPositive,'rows');
    
    realNegative = [negativeBC;negativeCB];
    realNegative = unique(realNegative,'rows');
    
    testPositive = unique([positiveAB;positiveAC],'rows');
    testNegative = unique([negativeAB;negativeAC],'rows');
    
    if numel(realPositive) == 0 || numel(testPositive) == 0
        truePositive = 0;
    else
        truePositive = numel(intersect(realPositive,testPositive,'rows'))/2;
    end
    falsePositive = (numel(testPositive)/2) - truePositive;
    
    if numel(realNegative) == 0 || numel(testNegative) == 0
        trueNegative = 0;
    else
        trueNegative = numel(intersect(realNegative,testNegative,'rows'))/2;
    end
 
    
    falseNegative = (numel(testNegative)/2) - trueNegative;
    
    disp([truePositive,falsePositive,trueNegative,falseNegative,numel(realNegative)/2,numel(realPositive)/2,numel(testNegative)/2,numel(testPositive)/2]);

    positiveWeight = (truePositive)/(numel(realPositive)/2+numel(realNegative)/2);
    negativeWeight = (falseNegative)/(numel(realPositive)/2+numel(realNegative)/2);
    if (truePositive+falseNegative)==0
        sensitivity = positiveWeight;
    else
        sensitivity = ((truePositive/(truePositive+falseNegative)))*positiveWeight;
    end
    
    %specificity = (trueNegative/(trueNegative+falsePositive));
    
    precision = truePositive/(truePositive+falsePositive)*positiveWeight;
    
    if (falsePositive+trueNegative) == 0
        fpr = negativeWeight;
    else
        fpr = falsePositive/(falsePositive+trueNegative)*negativeWeight;
    end;
    
end