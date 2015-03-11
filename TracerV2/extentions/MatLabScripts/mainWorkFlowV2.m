%mainWorkFlowV2
%The Main Work Flows
%Notes:
%originalPoints(1:A,2:B,3:C)
%samples(1:AB,2:AC,3:BC,4:BA,5:CA,6:CB)
%
%draw Samples Figure (Distance Bar)
%drawSamplesFigure;
%draw DistanceCircle
drawDistanceCircle(sample1,sample2,sample3,sample4,sample5,sample6);

RealPositivePoints = [];
RealPositivePoints = [RealPositivePoints;compareOtherTwoSamples(originalPoints1,sample4,sample5)];
RealPositivePoints = [RealPositivePoints;compareOtherTwoSamples(originalPoints2,sample1,sample6)];
RealPositivePoints = [RealPositivePoints;compareOtherTwoSamples(originalPoints3,sample2,sample3)];

percentage = numel(RealPositivePoints)/(numel(originalPoints1)+numel(originalPoints2)+numel(originalPoints3));
disp(percentage);
