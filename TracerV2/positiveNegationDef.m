%TP FP TN FN Defnintion
function [sensitivity,specificity,precision] = positiveNegationDef(pointsA,array1,array2)
    %getABset
    positiveAB = [];
    negativeAB = [];
    positiveAC = [];
    negativeAC = [];    
    for i = 1 : numel(array1)
        if array1(i) <= 3
            positiveAB(end+1,:) = pointsA(i,:);
        else
            negativeAB(end+1,:) = pointsA(i,:);
        end
    end
    
    for i = 1 : numel(array2)
        if array2(i) <= 3
            positiveAC(end+1,:) = pointsA(i,:);
        else
            negativeAC(end+1,:) = pointsA(i,:);
        end
    end
    
    positiveAB = unique(positiveAB,'rows');
    positiveAC = unique(positiveAC,'rows');
    negativeAB = unique(negativeAB,'rows');
    negativeAC = unique(negativeAC,'rows');
    
    truePositive = numel(intersect(positiveAB,positiveAC,'rows'))/2*2;
    falsePositive = (numel(positiveAB)+numel(positiveAC))/2 - truePositive;
    
    trueNegative = numel(intersect(negativeAB,negativeAC,'rows'))/2*2;
    falseNegative = (numel(negativeAB)+numel(negativeAC))/2 - trueNegative;
    
    sensitivity = truePositive/(truePositive+falseNegative);
    specificity = trueNegative/(trueNegative+falsePositive);
    precision = truePositive/(truePositive+falsePositive);
end