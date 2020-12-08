

extern int yymain(Platform::Array<Platform::String^>^ _args);

[Platform::MTAThread]
int main(Platform::Array<Platform::String^>^ _args)
{
	yymain( _args );
}
