package quicklic.floating.api;

interface FloatingInterfaceAIDL {
	int setDrawableQuicklic();
	void setFloatingVisibility(boolean value);
	int getFloatingVisibility();
	float setSize();
	boolean getAnimation();
	void touched(); 
	void doubleTouched();
	void longTouched();
}
